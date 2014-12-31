package com.mossle.bpm.web;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.mossle.bpm.persistence.domain.BpmConfBase;
import com.mossle.bpm.persistence.domain.BpmConfNode;
import com.mossle.bpm.persistence.manager.BpmConfBaseManager;
import com.mossle.bpm.persistence.manager.BpmConfNodeManager;
import com.mossle.bpm.persistence.manager.BpmConfUserManager;
import com.mossle.bpm.persistence.manager.BpmProcessManager;

import com.mossle.core.mapper.BeanMapper;

import org.activiti.engine.ProcessEngine;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("bpm")
public class BpmConfNodeController {
    private BpmConfUserManager bpmConfUserManager;
    private BpmConfNodeManager bpmConfNodeManager;
    private BpmConfBaseManager bpmConfBaseManager;
    private ProcessEngine processEngine;
    private BpmProcessManager bpmProcessManager;

    @RequestMapping("bpm-conf-node-list")
    public String list(@RequestParam("bpmConfBaseId") Long bpmConfBaseId,
            Model model) {
        BpmConfBase bpmConfBase = bpmConfBaseManager.get(bpmConfBaseId);
        List<BpmConfNode> bpmConfNodes = bpmConfNodeManager.findBy(
                "bpmConfBase", bpmConfBase);

        model.addAttribute("bpmConfNodes", bpmConfNodes);

        return "bpm/bpm-conf-node-list";
    }

    // ~ ======================================================================
    @Resource
    public void setBpmConfNodeManager(BpmConfNodeManager bpmConfNodeManager) {
        this.bpmConfNodeManager = bpmConfNodeManager;
    }

    @Resource
    public void setBpmConfBaseManager(BpmConfBaseManager bpmConfBaseManager) {
        this.bpmConfBaseManager = bpmConfBaseManager;
    }
}
