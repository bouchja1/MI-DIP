/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.bouchja1.mi_dip.rest.client.exception;

import java.util.Set;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;

/**
 *
 * @author jan
 */
public class EnsembleApiException extends RuntimeException {

    private static final long serialVersionUID = 6440534023347904569L;
    private Integer responseCode;
    private Set<ConstraintViolation<Object>> constraintViolations;

    public EnsembleApiException(Integer responseCode) {
        super();
        this.responseCode = responseCode;
    }

    public EnsembleApiException(Integer responseCode, String message) {
        super(message);
        this.responseCode = responseCode;
    }

    public EnsembleApiException(Integer responseCode, Throwable cause) {
        super(cause);
        this.responseCode = responseCode;
    }

    public EnsembleApiException(Set<ConstraintViolation<Object>> constraintViolations) {
        this(constraintViolations, HttpServletResponse.SC_BAD_REQUEST);
    }

    public EnsembleApiException(Set<ConstraintViolation<Object>> constraintViolations, Integer responseCode) {
        this.responseCode = responseCode;
        this.constraintViolations = constraintViolations;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public Set<ConstraintViolation<Object>> getConstraintViolations() {
        return constraintViolations;
    }
}
