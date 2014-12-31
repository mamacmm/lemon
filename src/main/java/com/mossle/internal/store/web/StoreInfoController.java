package com.mossle.internal.store.web;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import javax.servlet.http.HttpServletResponse;

import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.spring.MessageHelper;

import com.mossle.ext.export.Exportor;
import com.mossle.ext.export.TableModel;

import com.mossle.internal.store.domain.StoreInfo;
import com.mossle.internal.store.manager.StoreInfoManager;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/store")
public class StoreInfoController {
    private StoreInfoManager storeInfoManager;
    private MessageHelper messageHelper;
    private Exportor exportor;
    private BeanMapper beanMapper = new BeanMapper();

    @RequestMapping("store-info-list")
    public String list(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap, Model model) {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = storeInfoManager.pagedQuery(page, propertyFilters);
        model.addAttribute("page", page);

        return "store/store-info-list";
    }

    @RequestMapping("store-info-input")
    public String input(@RequestParam(value = "id", required = false) Long id,
            Model model) {
        if (id != null) {
            StoreInfo storeInfo = storeInfoManager.get(id);
            model.addAttribute("model", storeInfo);
        }

        return "store/store-info-input";
    }

    @RequestMapping("store-info-save")
    public String save(@ModelAttribute StoreInfo storeInfo,
            RedirectAttributes redirectAttributes) {
        Long id = storeInfo.getId();
        StoreInfo dest = null;

        if (id != null) {
            dest = storeInfoManager.get(id);
            beanMapper.copy(storeInfo, dest);
        } else {
            dest = storeInfo;
        }

        storeInfoManager.save(dest);
        messageHelper.addFlashMessage(redirectAttributes, "core.success.save",
                "保存成功");

        return "redirect:/store/store-info-list.do";
    }

    @RequestMapping("store-info-remove")
    public String remove(@RequestParam("selectedItem") List<Long> selectedItem,
            RedirectAttributes redirectAttributes) {
        List<StoreInfo> storeInfos = storeInfoManager.findByIds(selectedItem);
        storeInfoManager.removeAll(storeInfos);
        messageHelper.addFlashMessage(redirectAttributes,
                "core.success.delete", "删除成功");

        return "redirect:/store/store-info-list.do";
    }

    @RequestMapping("store-info-export")
    public void export(@ModelAttribute Page page,
            @RequestParam Map<String, Object> parameterMap,
            HttpServletResponse response) throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromMap(parameterMap);
        page = storeInfoManager.pagedQuery(page, propertyFilters);

        List<StoreInfo> storeInfos = (List<StoreInfo>) page.getResult();

        TableModel tableModel = new TableModel();
        tableModel.setName("store info");
        tableModel.addHeaders("id", "name");
        tableModel.setData(storeInfos);
        exportor.export(response, tableModel);
    }

    // ~ ======================================================================
    @Resource
    public void setStoreInfoManager(StoreInfoManager storeInfoManager) {
        this.storeInfoManager = storeInfoManager;
    }

    @Resource
    public void setMessageHelper(MessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Resource
    public void setExportor(Exportor exportor) {
        this.exportor = exportor;
    }
}
