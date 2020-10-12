package jp.co.axa.apidemo.enums;

public enum CRUDErrorStatus {
  UNKNOWN_FAILURE(1),
  ALREADY_EXISTS(2),
  TARGET_NOT_FOUND(3),
  VALIDATION_FAILURE(4);
  private final int status;
  CRUDErrorStatus(int status) {
    this.status = status;
  }
}
