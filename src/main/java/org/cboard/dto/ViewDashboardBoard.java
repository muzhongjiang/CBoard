package org.cboard.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.cboard.pojo.DashboardBoard;
import com.google.common.base.Function;
import org.cboard.services.role.RolePermission;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by yfyuan on 2016/8/23.
 */
@Getter
@Setter
@ToString
public class ViewDashboardBoard {

    private Long id;
    private String userId;
    private Long categoryId;
    private String name;
    private String userName;
    private String loginName;
    private String createTime;
    private String updateTime;
    private Map<String, Object> layout;
    private String categoryName;
    private boolean edit;
    private boolean delete;

    public static final Function TO = new Function<DashboardBoard, ViewDashboardBoard>() {
        @Nullable
        @Override
        public ViewDashboardBoard apply(@Nullable DashboardBoard input) {
            return new ViewDashboardBoard(input);
        }
    };

    public ViewDashboardBoard(DashboardBoard board) {
        this.id = board.getId();
        this.userId = board.getUserId();
        this.categoryId = board.getCategoryId();
        this.name = board.getName();
        this.userName = board.getUserName();
        this.loginName = board.getLoginName();
        this.createTime = board.getCreateTime().toString();
        this.updateTime = board.getUpdateTime().toString();
        this.layout = JSONObject.parseObject(board.getLayout());
        this.categoryName = board.getCategoryName();
        this.edit = RolePermission.isEdit(board.getPermission());
        this.delete = RolePermission.isDelete(board.getPermission());
    }


}
