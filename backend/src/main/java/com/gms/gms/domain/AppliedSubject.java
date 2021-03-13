package com.gms.gms.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 选题
 *
 * @author MrBird
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class AppliedSubject implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("ID")
    private Integer id;

    @TableField("SUB_ID")
    private String subId;

    @TableField("STU_ID")
    private Integer stuId;

    //未审核0，未通过1，已通过2
    @TableField("STATUS")
    private Integer status;

    @TableField("FEEDBACK")
    private String feedback;

    //分页参数
    @TableField(exist = false)
    private Integer page;

    @TableField(exist = false)
    private Integer size;

    @TableField(exist = false)
    private Subject subject;
}
