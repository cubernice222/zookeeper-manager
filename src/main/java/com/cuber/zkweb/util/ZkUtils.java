package com.cuber.zkweb.util;

import com.cuber.java.zkpros.constvar.ZooKeeperConst;
import com.cuber.java.zkpros.model.*;
import com.cuber.zkweb.model.ResponseMessage;
import com.cuber.zkweb.model.RoleEnum;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;

import com.cuber.zkweb.model.Page;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by cuber on 2016/10/13.
 */
public class ZkUtils {

    public static boolean createNode(ZooKeeperProsNode node, ZkClient zkClient){
        if(null != zkClient && node != null){
            String path = node.getParentPath() + "/" + node.getName();
            Gson gson = new Gson();
            if(!zkClient.exists(path)){
                zkClient.create(path,gson.toJson(node), CreateMode.PERSISTENT);
                return true;
            }
        }
        return false;
    }

    public static boolean  update(ZooKeeperProsNode node, ZkClient zkClient){
        if(null != zkClient && node != null){
            String path = node.getParentPath() + "/" + node.getName();
            Gson gson = new Gson();
            if(zkClient.exists(path)){
                zkClient.writeData(path,gson.toJson(node));
                return true;
            }
        }
        return false;
    }

    public static ZooKeeperProsNode getNode(String pathNode, ZkClient zkClient){
        if(!zkClient.exists(pathNode)){
            return null;
        }
        String nodeJson =  zkClient.readData(pathNode);
        if(ZooKeeperConst.ZKSPKEY.equals(pathNode)){
            return null;
        }
        try{
            Gson gson = new Gson();
            ZooKeeperProsNode returnObj = gson.fromJson(nodeJson,ZooKeeperProsNode.class);
            switch (returnObj.getType()) {
                case "1":
                    returnObj = gson.fromJson(nodeJson,ZooKeeperEnviromentNode.class);
                    break;
                case "2":
                    returnObj = gson.fromJson(nodeJson,ZooKeeperProjectNode.class);
                     break;
                case "3":
                    returnObj = gson.fromJson(nodeJson,ZooKeeperFolderNode.class);
                    break;
                default:
                    returnObj = gson.fromJson(nodeJson,ZooKeeperNode.class);
            }
            return returnObj;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static boolean deleteNode(ZooKeeperProsNode node, ZkClient zkClient){
        if(null != zkClient && node != null){
            String path = node.getParentPath() + "/" + node.getName();
            return zkClient.delete(path);
        }
        return false;
    }

    public static void copyNode(ZooKeeperProsNode node, ZkClient zkClient, String targetPath, boolean isOverWriten){
        String path = targetPath + "/" + node.getName();
        if(node instanceof ZooKeeperNode){
            node.setParentPath(targetPath);
            if(!zkClient.exists(path)){
                zkClient.create(path, node, CreateMode.PERSISTENT);
            }else{
                if(isOverWriten){
                    Gson gson = new Gson();
                    zkClient.writeData(path,gson.toJson(node));
                }
            }
        }else{
            List<String> nodeChildren= zkClient.getChildren(path);
            if(nodeChildren != null){
                nodeChildren.forEach(nodeName->{
                    copyNode(getNode(path + "/" + nodeName, zkClient),zkClient,targetPath + "/" + nodeName,isOverWriten);
                });
            }
        }
    }

    public static Page getPage(String path, Page page, ZkClient zkClient){
        if(zkClient.exists(path)){
            List<String> nodeNames = zkClient.getChildren(path);
            List<String> pageNodeNames = nodeNames;
            if(null == page){
                page =  new Page();
                page.setPageCount(-1);
            }else{
                List<List<String>> allPages = Lists.partition(nodeNames,page.getPageCount());
                if(allPages != null && allPages.size() >= page.getCurPage()){
                    pageNodeNames = allPages.get(page.getCurPage() - 1);
                }
                page.setTotalRecords(nodeNames.size());
            }
            List<ZooKeeperProsNode> result = Lists.newArrayList();
            if(pageNodeNames != null){
                pageNodeNames.stream().forEach(
                        node -> result.add(ZkUtils.getNode(path + "/" + node,zkClient)));
            }
            page.setData(result);

        }
        return page;
    }
    public static Page getFilter(String path, Page page, ZkClient zkClient,String filterValue){
        List<ZooKeeperProsNode> result = Lists.newArrayList();
        if(zkClient.exists(path)){
            List<String> nodeNames = zkClient.getChildren(path);
            List<ZooKeeperProsNode> allZkNodes = Lists.newArrayList();
            nodeNames.stream().forEach(nodeName -> allZkNodes.add(ZkUtils.getNode(path + "/" + nodeName,zkClient)));
            List<ZooKeeperProsNode> leftFilter = allZkNodes.stream().filter(
                    zkNode->(zkNode.getName().indexOf(filterValue) !=-1 && zkNode.getDesc().indexOf(filterValue) !=-1)
            ).collect(Collectors.toList());
            result = leftFilter;
            if(null == page){
                page =  new Page();
                page.setPageCount(-1);
            }else{
                List<List<ZooKeeperProsNode>> allPages = Lists.partition(leftFilter,page.getPageCount());
                if(allPages != null && allPages.size() >= page.getCurPage()){
                    result = allPages.get(page.getCurPage() - 1);
                }
                page.setTotalRecords(allPages.size());
            }
            page.setData(result);
        }
        return page;
    }

    public static List<ZooKeeperEnviromentNode> getCurrentUserVisualEnvNode(ZkClient zkClient){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        Page page = ZkUtils.getPage(ZooKeeperConst.ZKROOT,null,zkClient);
        if(page != null){
            List<ZooKeeperProsNode> allNode = page.getData();
            List<ZooKeeperEnviromentNode> left = new ArrayList<>();
            if(allNode != null && null != authorities && authorities.size() > 0){
                StringBuilder sb =  new StringBuilder();
                authorities.stream().forEach(authority -> sb.append(authority.getAuthority()).append(";"));
                String containStr = sb.toString();
                if(containStr.indexOf("ROLE_admin") > -1 ){
                    containStr = "ROLE_Zkdev;ROLE_Zksit;ROLE_Zkpre;ROLE_Zkprod;";
                }
                String containStrConst = containStr;
                allNode.stream().forEach(node -> {
                    if(node instanceof ZooKeeperEnviromentNode){
                        if(containStrConst.indexOf(((ZooKeeperEnviromentNode) node).getAccRule()) > -1)
                            left.add((ZooKeeperEnviromentNode)node);
                    }});
            }
            return left;
        }
        return null;
    }

    public static List<RoleEnum> getCurUserHasRole(ZkClient zkClient){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        List<RoleEnum> hasRoles = Lists.newArrayList();
        authorities.stream().forEach(authority -> {
            if("ROLE_admin".equals(authority.getAuthority())) {
                hasRoles.clear();
                hasRoles.addAll(Arrays.asList(RoleEnum.values()));
                return;
            }else{
                RoleEnum role = RoleEnum.getRoleByCode(authority.getAuthority());
                if(role != null)
                    hasRoles.add(role);
            }
        });
        return hasRoles;
    }
    public static boolean haveRightAccessNode(ZkClient zkClient, ZooKeeperProsNode node){
        boolean have = false;
        if(node != null && zkClient != null){
            String parentPath = node.getParentPath();
            if(node instanceof ZooKeeperEnviromentNode){//如果是环境节点
                ZooKeeperEnviromentNode envNode = (ZooKeeperEnviromentNode)node;
                UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
                if(authorities != null){
                    have = authorities.stream().anyMatch(authority -> authority.getAuthority().equals(envNode.getAccRule()) ||
                            authority.getAuthority().equals("ROLE_admin") );
                }
            }else{
                if(!Strings.isNullOrEmpty(parentPath)){
                    if(!(parentPath.indexOf(ZooKeeperConst.PUBLICCONFIG) > -1)){
                        ZooKeeperEnviromentNode env = getBySubNodePath(parentPath,zkClient);
                        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
                        if(authorities != null){
                            have = authorities.stream().anyMatch(authority -> authority.getAuthority().equals(env.getAccRule()) ||
                                    authority.getAuthority().equals("ROLE_admin") );
                        }
                    }else{
                        have = true;
                    }
                }
            }
        }
        return have;
    }

    public static ZooKeeperEnviromentNode getBySubNodePath(String nodePath,ZkClient zkClient){
        if(nodePath != null && nodePath.startsWith(ZooKeeperConst.ZKROOT)
                && !nodePath.startsWith(ZooKeeperConst.PUBLICCONFIG)
                && !nodePath.startsWith(ZooKeeperConst.ZKSPKEY)){
            String envleft = nodePath.substring(ZooKeeperConst.ZKROOT.length() + 1);
            String envNodeName = envleft.substring(0, envleft.indexOf('/') > -1 ? envleft.indexOf('/') :envleft.length());
            return (ZooKeeperEnviromentNode)getNode(ZooKeeperConst.ZKROOT + "/" + envNodeName,zkClient);
        }
        return null;
    }

    public static void constructMenu(final Model model, ZkClient zkClient){
        List<ZooKeeperEnviromentNode> accessEnvs = ZkUtils.getCurrentUserVisualEnvNode(zkClient);
        model.addAttribute("accessEnvs",accessEnvs);
    }

    public static ResponseMessage editNode(ZooKeeperProsNode nodes, ZkClient zkClient, String editType){
        ResponseMessage responseMessage = new ResponseMessage();
        String message = null;
        if(ZkUtils.haveRightAccessNode(zkClient,nodes)){
            if("add".equals(editType)){
                responseMessage.setDone(ZkUtils.createNode(nodes,zkClient));
                message = "节点已存在";
            }else{
                responseMessage.setDone(ZkUtils.update(nodes,zkClient));
                message = "节点不存在";
            }
            if(responseMessage.isDone()){
                responseMessage.setMessage("成功");
            }else{
                responseMessage.setMessage(message);
            }
        }else{
            responseMessage.setMessage("没有权限做此操作");
        }
        return responseMessage;
    }
}
