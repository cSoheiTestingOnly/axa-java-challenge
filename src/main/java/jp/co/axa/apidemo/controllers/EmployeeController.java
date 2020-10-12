package jp.co.axa.apidemo.controllers;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import jp.co.axa.apidemo.entities.EmployeeEntity;
import jp.co.axa.apidemo.services.EmployeeService;
import jp.co.axa.apidemo.funcs.ResponseExceptionMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class EmployeeController {

  private EmployeeService employeeService;

  @Autowired
  public EmployeeController(EmployeeService employeeService) {
    this.employeeService = employeeService;
  }


  @GetMapping("/employees")
  @ResponseStatus(code = HttpStatus.OK)
  @ApiOperation(value = "Method to get all the employee.")
  public SimpleResponse<List<EmployeeEntity>> getEmployees() {
    return employeeService.retrieveEmployees().mapLeft(ResponseExceptionMapper.MAPPER)
      .mapRight(entities -> Collections.singletonMap("fetched", entities))
      .map(e -> {
        throw e;
      }, SimpleResponse::new);
  }

  @GetMapping("/employees/{employeeId}")
  @ApiResponses(value = {@ApiResponse(code = 400, message = "Employee ID is not numerable or minus."),
    @ApiResponse(code = 404, message = "Employee specified by the ID is not existing in the DB")})
  public SimpleResponse<EmployeeEntity> getEmployee(@PathVariable(name = "employeeId") Long employeeId) {
    if (Objects.isNull(employeeId) || employeeId < 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "employee ID is not correct");
    }
    return employeeService.getEmployee(employeeId)
      .map(entity -> Collections.singletonMap("fetched", entity))
      .map(SimpleResponse::new)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee with the given ID not exists."));
  }

  @PostMapping("/employees")
  @ResponseStatus(HttpStatus.CREATED)
  @ApiResponses({
    @ApiResponse(code = 409, message = "The user specified with the ID already exists in the DB."),
    @ApiResponse(code = 400, message = "Validation failure. The entity may have missed the necessary property.")
  })
  public SimpleResponse<EmployeeEntity> saveEmployee(@RequestBody EmployeeEntity employee) {
    if(Objects.isNull(employee) || (Objects.nonNull(employee.getId()) && employee.getId() < 0)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Given params are invalid.");
    }
    return employeeService.saveEmployee(employee).mapLeft(ResponseExceptionMapper.MAPPER)
      .mapRight(entity -> Collections.singletonMap("created", entity))
      .map(e -> {
        throw e;
      }, SimpleResponse::new);
  }

  @ResponseStatus(HttpStatus.OK)
  @DeleteMapping("/employees/{employeeId}")
  public SimpleResponse<EmployeeEntity> deleteEmployee(@PathVariable(name = "employeeId") Long employeeId) {
    return employeeService.deleteEmployee(employeeId)
      .mapRight(entity -> Collections.singletonMap("deleted", entity))
      .mapLeft(ResponseExceptionMapper.MAPPER)
      .map(e -> {
        throw e;
      }, SimpleResponse::new);
  }

  @PutMapping("/employees/{employeeId}")
  public SimpleResponse<EmployeeEntity> updateEmployee(@RequestBody EmployeeEntity employee,
                                                       @PathVariable(name = "employeeId") Long employeeId) {
    if (Objects.isNull(employeeId) || Objects.isNull(employee) || Objects.equals(employee.getId(), employeeId)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Given Employee ID is not correct.");
    }

    return employeeService.updateEmployee(employee).mapRight(result -> {
      val map = new HashMap<String, EmployeeEntity>();
      map.put("old", result.getOld());
      map.put("updated", result.getUpdated());
      return Collections.unmodifiableMap(map);
    }).mapLeft(ResponseExceptionMapper.MAPPER)
      .map(e -> {
        throw e;
      }, SimpleResponse::new);
  }

  /**
   * @param <T>
   */
  @Getter
  @AllArgsConstructor
  private static class SimpleResponse<T> {
    private final Map<String, T> result;
  }
}
