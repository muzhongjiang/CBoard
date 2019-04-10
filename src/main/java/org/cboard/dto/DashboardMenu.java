package org.cboard.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by yfyuan on 2016/12/21.
 */
@Getter
@Setter
@ToString
public class DashboardMenu {

    private long menuId;
    private long parentId;
    private String menuName;
    private String menuCode;

    public DashboardMenu() {
    }
    public DashboardMenu(long menuId, long parentId, String menuName, String menuCode) {
        this.menuId = menuId;
        this.parentId = parentId;
        this.menuName = menuName;
        this.menuCode = menuCode;
    }

}
