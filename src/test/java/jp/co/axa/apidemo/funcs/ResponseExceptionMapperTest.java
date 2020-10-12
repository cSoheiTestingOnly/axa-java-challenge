package jp.co.axa.apidemo.funcs;

import jp.co.axa.apidemo.enums.CRUDErrorStatus;
import lombok.val;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ResponseExceptionMapperTest {
  @Test
  public void testMapping() {
    val notFound = ResponseExceptionMapper.MAPPER.apply(CRUDErrorStatus.TARGET_NOT_FOUND);
    assertThat(notFound.getStatus(), is(HttpStatus.NOT_FOUND));
    val validationFailure = ResponseExceptionMapper.MAPPER.apply(CRUDErrorStatus.VALIDATION_FAILURE);
    assertThat(validationFailure.getStatus(), is(HttpStatus.BAD_REQUEST));
    val conflict = ResponseExceptionMapper.MAPPER.apply(CRUDErrorStatus.ALREADY_EXISTS);
    assertThat(conflict.getStatus(), is(HttpStatus.CONFLICT));
    val unknown = ResponseExceptionMapper.MAPPER.apply(CRUDErrorStatus.UNKNOWN_FAILURE);
    assertThat(unknown.getStatus(), is(HttpStatus.INTERNAL_SERVER_ERROR));
  }
}
