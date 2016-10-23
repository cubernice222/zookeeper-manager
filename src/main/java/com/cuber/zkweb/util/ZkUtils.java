package com.cuber.zkweb.util;

import com.cuber.java.zkpros.constvar.ZooKeeperConst;
import com.cuber.java.zkpros.model.*;
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

import javax.servlet.http.HttpServletRequest;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
        System.out.println(pathNode);
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

    public static <T> T convert(Class<T> targetClass, HttpServletRequest request) {
        HashMap paraMap = new HashMap(request.getParameterMap());

        try {
            T e = targetClass.newInstance();
            BeanWrapperImpl bean = new BeanWrapperImpl(e);
            PropertyDescriptor[] propertyDescriptors = bean.getPropertyDescriptors();
            PropertyDescriptor[] arr$ = propertyDescriptors;
            int len$ = propertyDescriptors.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                PropertyDescriptor property = arr$[i$];
                String propertyName = property.getDisplayName();
                Object value = paraMap.get(propertyName);
                if(value instanceof String[]) {
                    String[] tem = (String[])((String[])value);
                    value = tem[0];
                }

                if(!"class".equals(propertyName)) {
                    bean.setPropertyValue(propertyName, value == null?null:value.toString().trim());
                }
            }

            return e;
        } catch (InstantiationException var13) {
            var13.printStackTrace();
        } catch (IllegalAccessException var14) {
            var14.printStackTrace();
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
                if(allPages != null && allPages.size() >= page.getCurPageIndex()){
                    pageNodeNames = allPages.get(page.getCurPageIndex() - 1);
                }
                page.setPages(allPages.size());
            }
            List<ZooKeeperProsNode> result = Lists.newArrayList();
            if(pageNodeNames != null){
                pageNodeNames.stream().forEach(
                        node -> result.add(ZkUtils.getNode(path + "/" + node,zkClient)));
            }
            page.setZooKeeperProsNodes(result);
            page.setCount(result.size());

        }
        return page;
    }

    public static List<ZooKeeperEnviromentNode> getCurrentUserVisualEnvNode(ZkClient zkClient){
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        Page page = ZkUtils.getPage(ZooKeeperConst.ZKROOT,null,zkClient);
        if(page != null){
            List<ZooKeeperProsNode> allNode = page.getZooKeeperProsNodes();
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

    public static void haveRightAccessNode(ZkClient zkClient, ZooKeeperProsNode node){
        boolean have = false;
        if(node != null && zkClient != null){
            String parentPath = node.getParentPath();
            if(Strings.isNullOrEmpty(parentPath)){
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
        if(!have){
            throw new AccessDeniedException("has no right access");
        }
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

    public static void main(String[] args) {
        System.out.println("123".substring(2));
    }
}
