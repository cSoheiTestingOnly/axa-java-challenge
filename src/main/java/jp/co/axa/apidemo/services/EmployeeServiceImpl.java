package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.entities.EmployeeEntity;
import jp.co.axa.apidemo.enums.CRUDErrorStatus;
import jp.co.axa.apidemo.fanctor.Either;
import jp.co.axa.apidemo.repositories.EmployeeRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {

  private EmployeeRepository employeeRepository;

  @Autowired
  public EmployeeServiceImpl(EmployeeRepository repository) {
    this.employeeRepository = repository;
  }


  @Override
  public Either<CRUDErrorStatus, List<EmployeeEntity>> retrieveEmployees() {
    try {
      return Either.right(Optional.ofNullable(employeeRepository.findAll()).orElse(Collections.emptyList()));
    } catch (Exception e) {
      log.error("Unknown Error Occurred while fetching all the employees.", e);
      return Either.left(CRUDErrorStatus.UNKNOWN_FAILURE);
    }
  }

  @Override
  public Optional<EmployeeEntity> getEmployee(Long employeeId) {
    return
      Optional.ofNullable(employeeId)
        .flatMap(id -> employeeRepository.findById(id));
  }

  @Override
  public Either<CRUDErrorStatus, EmployeeEntity> saveEmployee(@NonNull EmployeeEntity employee) {
    try {
      val emp = Optional.ofNullable(employee).map(EmployeeEntity::getId).flatMap(this::getEmployee);
      if (emp.isPresent()) {
        log.error("Employee with the given ID is already present, id is {}", emp.get().getId());
        return Either.left(CRUDErrorStatus.ALREADY_EXISTS);
      }
      return this.validateAndSave(employee);
    } catch (Exception e) {
      log.error("Failed in saving the employee with ID {}",
        Optional.ofNullable(employee)
          .map(EmployeeEntity::getId)
          .map(Object::toString)
          .orElse(""), e);
      return Either.left(CRUDErrorStatus.UNKNOWN_FAILURE);
    }
  }

  @Override
  public Either<CRUDErrorStatus, EmployeeEntity> deleteEmployee(Long employeeId) {
    try {
      val old = this.getEmployee(employeeId);
      if (old.isPresent()) {
        employeeRepository.deleteById(employeeId);
        return Either.right(old.get());
      }
      return Either.right(null);
    } catch (Exception e) {
      log.error("Error occurred while deleting the employee with ID {}", employeeId);
      return Either.left(CRUDErrorStatus.UNKNOWN_FAILURE);
    }
  }

  @Override
  public Either<CRUDErrorStatus, UpdateResult> updateEmployee(EmployeeEntity employee) {
    try {
      val old = Optional.ofNullable(employee).map(EmployeeEntity::getId).flatMap(this::getEmployee);
      if (!old.isPresent()) {
        return Either.left(CRUDErrorStatus.TARGET_NOT_FOUND);
      }
      return this.validateAndSave(employee).mapRight((updated) -> new UpdateResult(old.get(), updated));
    } catch (Exception e) {
      log.error("Failed in updating the employee with ID {}",
        Optional.ofNullable(employee).map(EmployeeEntity::getId).orElse(null));
      return Either.left(CRUDErrorStatus.UNKNOWN_FAILURE);
    }
  }

  private Either<CRUDErrorStatus, EmployeeEntity> validateAndSave(EmployeeEntity employee) {
    if (!Optional.ofNullable(employee).filter(EmployeeEntity::validateSelf).isPresent()) {
      log.warn("Failed in creating an employee record due to the validation failure.");
      return Either.left(CRUDErrorStatus.VALIDATION_FAILURE);
    }
    return Either.right(employeeRepository.saveAndFlush(employee));
  }
}