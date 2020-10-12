package jp.co.axa.apidemo.services;


import jp.co.axa.apidemo.entities.EmployeeEntity;
import jp.co.axa.apidemo.enums.CRUDErrorStatus;
import jp.co.axa.apidemo.repositories.EmployeeRepository;
import lombok.val;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;


import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class EmployeeServiceImplTest {
  @Mock
  private EmployeeRepository employeeRepository;

  private static final EmployeeEntity TEST_EMPLOYEE_1 = new EmployeeEntity(1L, "Chiakma", 1000, "Some Dept");
  private static final EmployeeEntity TEST_EMPLOYEE_2 = new EmployeeEntity(2L, "Chiakma 2", 1500, "Some Dept 2");

  @Before
  public void before() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testGetAllNormal() {
    when(employeeRepository.findAll()).thenReturn(Arrays.asList(TEST_EMPLOYEE_1, TEST_EMPLOYEE_2));
    val service = new EmployeeServiceImpl(this.employeeRepository);
    val result = service.retrieveEmployees();
    assertThat(result.isRight(), is(true));
    assertThat(result.getRight(), is(not(empty())));
    assertThat(result.getRight(), hasSize(2));
  }
  @Test
  public void testGetAllWithEmpty() {
    val service = new EmployeeServiceImpl(this.employeeRepository);
    val result = service.retrieveEmployees();
    assertThat(result.isRight(), is(true));
    assertThat(result.getRight(), is(not(nullValue())));
    assertThat(result.getRight(), is(empty()));
  }
  @Test
  public void testGetAllWithError() {
    when(employeeRepository.findAll()).thenThrow(new RuntimeException());
    val service = new EmployeeServiceImpl(this.employeeRepository);
    val result = service.retrieveEmployees();
    assertThat(result.isLeft(), is(true));
    assertThat(result.getLeft(), is(CRUDErrorStatus.UNKNOWN_FAILURE));
  }


  @Test
  public void testGetSingle() {
    when(this.employeeRepository.findById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(TEST_EMPLOYEE_1));
    val result = new EmployeeServiceImpl(this.employeeRepository).getEmployee(1L);
    assertThat(result.isPresent(), is(true));
    assertThat(result.get(), is(CoreMatchers.equalTo(TEST_EMPLOYEE_1)));
  }
  @Test
  public void testGetSingleWithNullId() {
    when(this.employeeRepository.findById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(TEST_EMPLOYEE_1));
    val result = new EmployeeServiceImpl(this.employeeRepository).getEmployee(null);
    assertThat(result.isPresent(), is(false));
  }
  @Test
  public void testGetSingleWithNonExists() {
    when(this.employeeRepository.findById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.empty());
    val result = new EmployeeServiceImpl(this.employeeRepository).getEmployee(3L);
    assertThat(result.isPresent(), is(false));
  }

  @Test
  public void testSave() {
    when(this.employeeRepository.saveAndFlush(ArgumentMatchers.any(EmployeeEntity.class))).thenReturn(TEST_EMPLOYEE_1);
    val testEmpWithNoId = TEST_EMPLOYEE_1.withId(null);
    val result = new EmployeeServiceImpl(this.employeeRepository).saveEmployee(testEmpWithNoId);
    assertThat(result.isRight(), is(true));
    assertThat(result.getRight(), is(TEST_EMPLOYEE_1));
  }

  @Test
  public void testSaveWithConflict() {
    when(this.employeeRepository.findById(1L)).thenReturn(Optional.of(TEST_EMPLOYEE_1));
    val result = new EmployeeServiceImpl(this.employeeRepository).saveEmployee(TEST_EMPLOYEE_1);
    assertThat(result.isLeft(), is(true));
    assertThat(result.getLeft(), is(CRUDErrorStatus.ALREADY_EXISTS));
  }

  @Test
  public void testSaveWithValidationFailure() {
    val testEmpWithNoName = TEST_EMPLOYEE_1.withName("");
    val result = new EmployeeServiceImpl(this.employeeRepository).saveEmployee(testEmpWithNoName);
    assertThat(result.isLeft(), is(true));
    assertThat(result.getLeft(), is(CRUDErrorStatus.VALIDATION_FAILURE));
  }

  @Test
  public void testUpdate() {
    val testEmpWithDifferentSalary = TEST_EMPLOYEE_1.withSalary(20000);
    when(this.employeeRepository.findById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(TEST_EMPLOYEE_1));
    when(this.employeeRepository.saveAndFlush(ArgumentMatchers.any(EmployeeEntity.class))).thenReturn(testEmpWithDifferentSalary);
    val result = new EmployeeServiceImpl(this.employeeRepository).updateEmployee(testEmpWithDifferentSalary);
    assertThat(result.isRight(), is(true));
    assertThat(result.getRight(), is(not(nullValue())));
    assertThat(result.getRight().getOld(), is(equalTo(TEST_EMPLOYEE_1)));
    assertThat(result.getRight().getUpdated(), is(equalTo(testEmpWithDifferentSalary)));
  }

  @Test
  public void testUpdateWithNotFound() {
    val testEmpWithDifferentSalary = TEST_EMPLOYEE_1.withSalary(20000);
    when(this.employeeRepository.findById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.empty());
    val result = new EmployeeServiceImpl(this.employeeRepository).updateEmployee(testEmpWithDifferentSalary);
    assertThat(result.isLeft(), is(true));
    assertThat(result.getLeft(), is(CRUDErrorStatus.TARGET_NOT_FOUND));
  }
  @Test
  public void testUpdateWithError() {
    val testEmpWithDifferentSalary = TEST_EMPLOYEE_1.withSalary(20000);
    when(this.employeeRepository.findById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(TEST_EMPLOYEE_1));
    when(this.employeeRepository.saveAndFlush(ArgumentMatchers.any(EmployeeEntity.class))).thenThrow(new RuntimeException());
    val result = new EmployeeServiceImpl(this.employeeRepository).updateEmployee(testEmpWithDifferentSalary);
    assertThat(result.isLeft(), is(true));
    assertThat(result.getLeft(), is(CRUDErrorStatus.UNKNOWN_FAILURE));
  }
  @Test
  public void testUpdateWitValidationFailure() {
    val testEmpWithDifferentSalary = TEST_EMPLOYEE_1.withName(null);
    when(this.employeeRepository.findById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(TEST_EMPLOYEE_1));
    val result = new EmployeeServiceImpl(this.employeeRepository).updateEmployee(testEmpWithDifferentSalary);
    assertThat(result.isLeft(), is(true));
    assertThat(result.getLeft(), is(CRUDErrorStatus.VALIDATION_FAILURE));
  }

  @Test
  public void testDelete() {
    when(this.employeeRepository.findById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(TEST_EMPLOYEE_1));
    val result = new EmployeeServiceImpl(this.employeeRepository).deleteEmployee(TEST_EMPLOYEE_1.getId());
    assertThat(result.isRight(), is(true));
    assertThat(result.getRight(), is(equalTo(TEST_EMPLOYEE_1)));
  }
  @Test
  public void testDeleteWithNoExistent() {
    val result = new EmployeeServiceImpl(this.employeeRepository).deleteEmployee(TEST_EMPLOYEE_1.getId());
    assertThat(result.isRight(), is(true));
    assertThat(result.getRight(), is(nullValue()));
  }

  @Test
  public void testDeleteWithError() {
    when(this.employeeRepository.findById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(TEST_EMPLOYEE_1));
    doThrow(new RuntimeException()).when(this.employeeRepository).deleteById(ArgumentMatchers.any(Long.class));
    val result = new EmployeeServiceImpl(this.employeeRepository).deleteEmployee(TEST_EMPLOYEE_1.getId());
    assertThat(result.isLeft(), is(true));
    assertThat(result.getLeft(), is(CRUDErrorStatus.UNKNOWN_FAILURE));

  }
}
