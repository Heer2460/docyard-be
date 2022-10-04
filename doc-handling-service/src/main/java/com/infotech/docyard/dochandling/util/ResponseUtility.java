package com.infotech.docyard.dochandling.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.infotech.docyard.dochandling.dto.BaseDTO;
import com.infotech.docyard.dochandling.exceptions.CustomException;
import com.infotech.docyard.dochandling.exceptions.DBConstraintViolationException;
import com.infotech.docyard.dochandling.exceptions.NoDataFoundException;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.history.Revision;
import org.springframework.data.history.RevisionMetadata;
import org.springframework.data.history.Revisions;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@Log4j2
public class ResponseUtility {

    private static final ResourceBundle messageBundle = ResourceBundle.getBundle("messages");

    @NoArgsConstructor
    @Data
    public static class APIResponse {
        private Object data;
        private String message;

        public APIResponse(Object data, String message) {
            this.data = data;
            this.message = message;
        }
    }

    public static APIResponse buildAPIResponse(Object data, String message) {
        APIResponse response = new APIResponse();
        response.setMessage(
                AppUtility.isEmpty(message)
                        ? messageBundle.getString("generic.success")
                        : message
        );
        response.setData(AppUtility.isEmpty(data) ? null : data);
        return response;
    }

    /**
     * Exception Response
     *
     * @param e
     * @param DBConstraint
     * @throws CustomException
     */
    public static void exceptionResponse(Exception e, String... DBConstraint) throws CustomException {
        //e.printStackTrace();
        log.error(e);
        if (!AppUtility.isEmpty(DBConstraint)) {
            ResponseUtility.validateDBConstraint(e, "", DBConstraint);
        } else if (e instanceof EmptyResultDataAccessException) {
            throw new NoDataFoundException("No Data Found.", e);
        }
        throw new CustomException(e.getMessage(), e);
    }

    /*
      Generate created response
      This method will not be used locally

      @param data
      @return
    */
    @SuppressWarnings("rawtypes")
    public static CustomResponse createdResponse(Object data, String responseMessage) {
        return CustomResponse
                .status(HttpStatus.CREATED)
                .body(buildAPIResponse(data, responseMessage));
    }

    /**
     * Generate success response for Delete Accepted.
     *
     * @param data
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static CustomResponse deleteSuccessResponse(Object data, String responseMessage) {
        return CustomResponse
                .status(HttpStatus.ACCEPTED)
                .body(buildAPIResponse(data, responseMessage));
    }

    /**
     * Generate success response
     * This method will not be used locally
     *
     * @param data
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static CustomResponse successResponse(Object data, String responseMessage) {
        return CustomResponse
                .status(HttpStatus.OK)
                .body(buildAPIResponse(data, responseMessage));
    }

    /**
     * Generate success response
     * This method will not be used locally
     *
     * @param data
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static CustomResponse notContentResponse(Object data, String responseMessage) {
        return CustomResponse
                .status(HttpStatus.NO_CONTENT)
                .body(buildAPIResponse(data, responseMessage));
    }

    /**
     * Generate success response
     * This method will not be used locally
     *
     * @param data
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static CustomResponse lockedUserResponse(Object data, String responseMessage) {
        return CustomResponse
                .status(HttpStatus.LOCKED)
                .body(buildAPIResponse(data, responseMessage));
    }

    /**
     * Generate success response to create a record
     *
     * @param data
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static CustomResponse successResponseForPost(Object data) throws CustomException, DBConstraintViolationException {
        return CustomResponse
                .status(HttpStatus.CREATED)
                .body(data);
    }

    /**
     * Generate success response to create a record
     *
     * @param data
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static CustomResponse successResponseForPut(Object data, String responseMessage) throws CustomException, DBConstraintViolationException {
        return CustomResponse
                .status(HttpStatus.RESET_CONTENT)
                .body(buildAPIResponse(data, responseMessage));
    }

    /**
     * Generate generic response for list data
     *
     * @param optionalList
     * @param baseObject
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static <B, E> CustomResponse buildResponseList(Optional<List<E>> optionalList, BaseDTO<B, E> baseObject) throws CustomException, NoDataFoundException {
        return buildResponseList(AppUtility.isEmpty(optionalList) ? null : optionalList.get(), baseObject);
    }


    /**
     * Generate generic revision response for list data
     *
     * @param revisionList
     * @param baseObject
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static <B, E> CustomResponse buildRevisionResponseList(Revisions<Integer, E> revisionList, BaseDTO<B, E> baseObject, Boolean... partialFill) throws CustomException, NoDataFoundException {
        if (!AppUtility.isEmpty(revisionList.getContent())) {
            List<B> data = new ArrayList<B>(revisionList.getContent().size());
            B bo;
            Boolean pFill = true;
            if (!AppUtility.isEmpty(partialFill)) {
                pFill = partialFill[0];
            }
            Integer revision = 1;
            for (Revision<Integer, E> obj : revisionList.getContent()) {
                RevisionMetadata<Integer> metatdata = obj.getMetadata();
                if (!metatdata.getRevisionType().equals(RevisionMetadata.RevisionType.DELETE)) {
                    bo = baseObject.convertToNewDTO(obj.getEntity(), pFill);
                    data.add(bo);
                }
            }
            return ResponseUtility.successResponse(data, revisionList.getContent().size() + " Records Found!");
        }
        throw new NoDataFoundException();
    }

    /**
     * Generate generic response for list data
     *
     * @param listOfEntities
     * @param baseObject
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static <B, E> CustomResponse buildResponseList(List<E> listOfEntities, BaseDTO<B, E> baseObject, Boolean... partialFill) throws CustomException, NoDataFoundException {
        if (!AppUtility.isEmpty(listOfEntities)) {
            List<B> data = new ArrayList<B>(listOfEntities.size());
            B bo;
            Boolean pFill = true;
            if (!AppUtility.isEmpty(partialFill)) {
                pFill = partialFill[0];
            }
            for (E obj : listOfEntities) {
                bo = baseObject.convertToNewDTO(obj, pFill);
                data.add(bo);
            }
            return ResponseUtility.successResponse(data, listOfEntities.size() + " Records Found!");
        }
        throw new NoDataFoundException();
    }

    public static <B, E> List<B> buildDTOList(List<E> listOfEntities, BaseDTO<B, E> baseObject, Boolean... partialFill) {
        List<B> data = new ArrayList<>();
        if (!AppUtility.isEmpty(listOfEntities)) {
            data = new ArrayList<B>(listOfEntities.size());
            B bo;
            Boolean pFill = true;
            if (!AppUtility.isEmpty(partialFill)) {
                pFill = partialFill[0];
            }
            for (E obj : listOfEntities) {
                bo = baseObject.convertToNewDTO(obj, pFill);
                data.add(bo);
            }
        }
        return data;
    }

    /**
     * Generate generic response for list data
     *
     * @param listOfEntities
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static <B, E> CustomResponse buildResponseList(List<E> listOfEntities) throws CustomException, NoDataFoundException {
        if (!AppUtility.isEmpty(listOfEntities))
            return ResponseUtility.successResponse(listOfEntities, listOfEntities.size() + " Records Found!");
        throw new NoDataFoundException();
    }

    /**
     * Generate generic response for single Object data
     */
    @SuppressWarnings("rawtypes")
    public static <B, E> CustomResponse buildResponseObject(E entityObject, BaseDTO<B, E> baseObject, boolean partialFill) throws NoDataFoundException {
        if (!AppUtility.isEmpty(entityObject))
            return ResponseUtility.successResponse(baseObject.convertToNewDTO(entityObject, partialFill), "Valid Object");
        throw new NoDataFoundException();
    }

    /**
     * Generate generic response for single Optional Object data
     */
    @SuppressWarnings("rawtypes")
    public static <B, E> CustomResponse buildResponseObject(Optional<E> entityObject, BaseDTO<B, E> baseObject, boolean partialFill) throws CustomException, NoDataFoundException {
        if (!AppUtility.isEmpty(entityObject))
            return ResponseUtility.successResponse(baseObject.convertToNewDTO(entityObject.get(), partialFill), "Valid Object");
        throw new NoDataFoundException();
    }

    public static CustomResponse buildResponseObjectDTO(Optional entityObject, BaseDTO baseObject, boolean partialFill) throws CustomException, NoDataFoundException {
        if (!AppUtility.isEmpty(entityObject))
            return ResponseUtility.successResponse(baseObject.convertToNewDTO(entityObject.get(), partialFill), "Valid Object");
        throw new NoDataFoundException();
    }

    /**
     * Generate generic response for single Object data
     *
     * @param entityObject
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static <B, E> CustomResponse buildResponseObject(E entityObject) throws NoDataFoundException {
        if (!AppUtility.isEmpty(entityObject))
            return ResponseUtility.successResponse(entityObject, "Valid Object");
        throw new NoDataFoundException();
    }

    public static ResponseEntity<?> buildReportResponseObject(InputStreamResource isr) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/pdf");

        return new ResponseEntity<>(isr, headers, HttpStatus.OK);
    }

    /**
     * Generate generic response for list data while filtering the provided properties
     *
     * @param baseObject
     * @param listOfEntities
     * @return
     */
    @SuppressWarnings("rawtypes")
    public static <B, E> CustomResponse buildResponseListWithFilterProperties(List<E> listOfEntities, BaseDTO<B, E> baseObject, String... filterProperties) throws CustomException, NoDataFoundException {
        if (!AppUtility.isEmpty(listOfEntities)) {
            List<B> data = new ArrayList<B>(listOfEntities.size());
            B bo;
            for (E obj : listOfEntities) {
                bo = baseObject.convertToNewDTO(obj, false);
                data.add(bo);
            }
            ObjectMapper mapper = new ObjectMapper();
            try {
                FilterProvider filters = new SimpleFilterProvider().addFilter("customFilter", SimpleBeanPropertyFilter.filterOutAllExcept(filterProperties));
                return ResponseUtility.successResponse(mapper.writer(filters).writeValueAsString(data), "Valid Object");
            } catch (JsonProcessingException e) {
                throw new CustomException(e);
            }
        }
        throw new NoDataFoundException();
    }

    /**
     * Validates DB Constraints...
     *
     * @param exc
     * @param constraintName
     * @param value
     * @throws DBConstraintViolationException
     */
    public static void validateDBConstraint(Exception exc, String value, String... constraintName) throws DBConstraintViolationException {
        if (exc instanceof DataIntegrityViolationException) {
            if (exc.getCause() instanceof ConstraintViolationException) {
                ConstraintViolationException constraintException = (ConstraintViolationException) exc.getCause();
                for (String cn : constraintName) {
                    if (!AppUtility.isEmpty(constraintException.getCause().getMessage()) && constraintException.getCause().getMessage().toUpperCase().contains(cn.toUpperCase())) {
                        if (constraintException.getCause().getMessage().contains("DELETE")) {
                            value = cn + " " + AppUtility.getResourceMessage("exception.delete.association");
                        } else {
                            value = cn + " " + AppUtility.getResourceMessage("exception.already.exist");
                        }
                        if (constraintException.getCause() instanceof SQLIntegrityConstraintViolationException) {
                            SQLIntegrityConstraintViolationException integrityException = (SQLIntegrityConstraintViolationException) constraintException.getCause();
                            value = integrityException.getMessage();
                        }
                        throw new DBConstraintViolationException(value, exc);
                    }
                }
            }
        }
    }
}
