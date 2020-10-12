package jp.co.axa.apidemo.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Wither;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Optional;

@Entity
@Table(name="EMPLOYEE")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Wither
@ApiModel(value = "Employee", description = "Model to express the employee information.")
public class EmployeeEntity {

    @Getter
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @ApiModelProperty(value = "ID of employee. Optional when creating the new record.", dataType = "Number", allowableValues = "Numbers larger than 0")
    private Long id;

    @Getter
    @Column(name="EMPLOYEE_NAME")
    @ApiModelProperty(value = "Name of employee. This property is mandatory regardless of update or create.", required = true)
    private String name;

    @Getter
    @Column(name="EMPLOYEE_SALARY")
    @ApiModelProperty(value = "Current salary of the employee.", notes = "Currency is JPY(¥).", dataType = "Number")
    private Integer salary;

    @Getter
    @Column(name="DEPARTMENT")
    @ApiModelProperty(value = "The department where employee belongs to.")
    private String department;

    // FIXME better to isolate the validation logic from the entity
    public boolean validateSelf() {
      if (StringUtils.isEmpty(this.name)) {
        return false;
      }
      if (this.id!= null && this.id < 0) {
        return false;
      }
      if (this.salary != null && this.salary < 0L) {
        return false;
      }
      return true;
    }

}
