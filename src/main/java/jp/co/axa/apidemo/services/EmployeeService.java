package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.entities.EmployeeEntity;
import jp.co.axa.apidemo.enums.CRUDErrorStatus;
import jp.co.axa.apidemo.fanctor.Either;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Wither;

import java.util.List;
import java.util.Optional;

/**
 * Class to process the business logic of the employees.<br/>
 */
public interface EmployeeService {

  /**
   * Method to get all the employees in the repo.
   *
   * @return Either of list of all the employee or error status.
   */
  Either<CRUDErrorStatus, List<EmployeeEntity>> retrieveEmployees();

  /**
   * Get an EmployeeEntity specified by the ID.
   *
   * @param employeeId id of employee.
   * @return EmployeeEntity Entity wrapped with Optional functor if there is an user with the ID.
   * Returns Empty instance if the user not exists.
   */
  Optional<EmployeeEntity> getEmployee(Long employeeId);

  /**
   * Method to save an employee data into the DB.
   * This method is expected to be used to create new record and fails if the user with given ID already exists in the DB.
   * If you would like to update the existing record, use {@link EmployeeService#updateEmployee}.
   *
   * @param employee employee data to be saved. ID is not mandatory, but recommended to be set to guarantee the idempotence.
   * @return Either of error status or created employee entity with the generated ID (if not present).
   */
  Either<CRUDErrorStatus, EmployeeEntity> saveEmployee(EmployeeEntity employee);

  /**
   * Method to delete an employee with the given ID.
   *
   * @param employeeId 　ID of the employee to delete.
   * @return Either of error status or deleted employee entity.
   */
  Either<CRUDErrorStatus, EmployeeEntity> deleteEmployee(Long employeeId);

  /**
   * Method to update the employee entity. <br/>
   * This method is to be update and do not create new record even if the entity with the given ID is present.
   * If you wanna create an entity, use {@link EmployeeService#saveEmployee(EmployeeEntity)}.
   *
   * @param employee Employee Entity to be updated.
   * @return Either of error status or update result. Update result contains both of the updated instance and previous
   * status.
   */
  Either<CRUDErrorStatus, UpdateResult> updateEmployee(EmployeeEntity employee);


  @AllArgsConstructor
  @Wither
  @Getter
  class UpdateResult {
    private final EmployeeEntity old;
    private final EmployeeEntity updated;
  }
}