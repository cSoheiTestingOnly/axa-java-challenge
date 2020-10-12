package jp.co.axa.apidemo.funcs;


import jp.co.axa.apidemo.enums.CRUDErrorStatus;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Function;

/**
 * Class to map the error status in the service layer to the exception in the controller.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResponseExceptionMapper implements Function<CRUDErrorStatus, ResponseStatusException> {

  public static ResponseExceptionMapper MAPPER = new ResponseExceptionMapper();
  @Override
  public ResponseStatusException apply(CRUDErrorStatus status) {
    switch (status) {
      case ALREADY_EXISTS:
        return new ResponseStatusException(HttpStatus.CONFLICT, "Target entity already exists.");
      case TARGET_NOT_FOUND:
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Target entity not found");
      case VALIDATION_FAILURE:
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Given data is not valid.");
      case UNKNOWN_FAILURE:
      default:
        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown Error occurred in the server.");
    }
  }
}
