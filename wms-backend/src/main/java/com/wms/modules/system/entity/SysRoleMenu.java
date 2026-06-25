package com.wms.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sys_role_menu")
public class SysRoleMenu implements Serializable {
    @TableId(type = IdType.INPUT)
    private Long roleId;
    @TableField("menu_id")
    private Long menuId;
    private Long createBy;
    private Long updateBy;
    private Integer deleted;
}
