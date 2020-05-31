package com.huawei.springbootweb.model.qo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class QueryQo {
    @ApiModelProperty(value="用户名",name="name",example="foo")
    @NotBlank(message = "聚合结果别名不能为空")
    private String name;

    @ApiModelProperty(value="用户名",name="info",example="bar")
    private String info;
}
