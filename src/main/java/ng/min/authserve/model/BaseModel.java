package ng.min.authserve.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ng.min.authserve.utils.CommonUtils;
import ng.min.authserve.utils.Validation;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class BaseModel<T> {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(hidden = true)
    private Long id;

    @ApiModelProperty(hidden = true)
	private LocalDateTime created = LocalDateTime.now();

    @ApiModelProperty(hidden = true)
    private LocalDateTime modified = LocalDateTime.now();

    public String getCreated() {
        return Validation.validData(created)? CommonUtils.dateToStringFormated(created):" ";
    }

    public String getModified() {
        return Validation.validData(modified)? CommonUtils.dateToStringFormated(modified):" ";
    }
}
