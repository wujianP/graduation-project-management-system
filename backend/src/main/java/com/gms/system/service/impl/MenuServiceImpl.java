package com.gms.system.service.impl;

import com.gms.common.domain.GmsConstant;
import com.gms.common.domain.Tree;
import com.gms.common.utils.TreeUtil;
import com.gms.system.dao.MenuMapper;
import com.gms.system.domain.Menu;
import com.gms.system.manager.UserManager;
import com.gms.system.service.MenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service("menuService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private UserManager userManager;

    @Override
    public List<Menu> findUserPermissions(String username) {
        return this.baseMapper.findUserPermissions(username);
    }

    @Override
    public List<Menu> findUserPermissionsWithStage(String username) {
        return this.baseMapper.findUserPermissionsWithStage(username);
    }

    @Override
    public List<Menu> findUserMenus(String realName) {
        return this.baseMapper.findUserMenus(realName);
    }

    @Override
    public List<Menu> findUserMenusWithStage(String realName) {
        return this.baseMapper.findUserMenusWithStage(realName);
    }

    @Override
    public Map<String, Object> findMenus(Menu menu) {
        Map<String, Object> result = new HashMap<>();
        try {
            LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
            List<Menu> menus = baseMapper.selectList(queryWrapper);

            List<Tree<Menu>> trees = new ArrayList<>();
            List<String> ids = new ArrayList<>();
            buildTrees(trees, menus, ids);

            result.put("ids", ids);
            if (StringUtils.equals(menu.getType(), GmsConstant.TYPE_BUTTON)) {
                result.put("rows", trees);
            } else {
                Tree<Menu> menuTree = TreeUtil.build(trees);
                result.put("rows", menuTree);
            }

            result.put("total", menus.size());
        } catch (NumberFormatException e) {
            log.error("查询菜单失败", e);
            result.put("rows", null);
            result.put("total", 0);
        }
        return result;
    }


    @Override
    public List<Menu> findMenuList(Menu menu) {
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Menu::getMenuId);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public void createMenu(Menu menu) {
        setMenu(menu);
        this.save(menu);
    }

//    @Override
//    @Transactional
//    public void updateMenu(Menu menu) throws Exception {
//        setMenu(menu);
//        baseMapper.updateById(menu);
//
//        // 查找与这些菜单/按钮关联的用户
//        List<String> userIds = this.baseMapper.findUserIdsByMenuId(String.valueOf(menu.getMenuId()));
//        // 重新将这些用户的角色和权限缓存到 Redis中
//        this.userManager.loadUserPermissionRoleRedisCache(userIds);
//    }

//    @Override
//    @Transactional
//    public void deleteMenus(String[] menuIds) throws Exception {
//        this.delete(Arrays.asList(menuIds));
//        for (String menuId : menuIds) {
//            // 查找与这些菜单/按钮关联的用户
//            List<String> userIds = this.baseMapper.findUserIdsByMenuId(String.valueOf(menuId));
//            // 重新将这些用户的角色和权限缓存到 Redis中
//            this.userManager.loadUserPermissionRoleRedisCache(userIds);
//        }
//    }

    private void buildTrees(List<Tree<Menu>> trees, List<Menu> menus, List<String> ids) {
        menus.forEach(menu -> {
            ids.add(menu.getMenuId().toString());
            Tree<Menu> tree = new Tree<>();
            tree.setId(menu.getMenuId().toString());
            tree.setParentId(menu.getParentId().toString());
            tree.setText(menu.getMenuName());
            tree.setComponent(menu.getComponent());
            tree.setPath(menu.getPath());
            tree.setOrder(menu.getOrderNum());
            tree.setType(menu.getType());
            trees.add(tree);
        });
    }

    private void setMenu(Menu menu) {
        if (menu.getParentId() == null)
            menu.setParentId(0);
        if (Menu.TYPE_BUTTON.equals(menu.getType())) {
            menu.setPath(null);
            menu.setComponent(null);
        }
    }


    private void delete(List<String> menuIds) {
        removeByIds(menuIds);

        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Menu::getParentId, menuIds);
        List<Menu> menus = baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(menus)) {
            List<String> menuIdList = new ArrayList<>();
            menus.forEach(m -> menuIdList.add(String.valueOf(m.getMenuId())));
            this.delete(menuIdList);
        }
    }

}
