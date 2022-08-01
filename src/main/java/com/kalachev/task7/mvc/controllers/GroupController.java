package com.kalachev.task7.mvc.controllers;

import java.util.List;

import com.kalachev.task7.service.GroupOptions;
import com.kalachev.task7.utilities.ValidationUtills;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class GroupController {

  static final String ERROR_PAGE = "error-page";
  static final String RESULT = "result";
  static final String VALID = "valid";
  static final String NOT_VALID = "not valid";

  @Autowired
  GroupOptions groupOptions;

  @GetMapping(value = "/filter-by-group-size")
  public String filterBySize() {
    return "choose-size-page";
  }

  @PostMapping("/filter-by-group-size")
  public String handleSizeFiltering(
      @RequestParam("minGroupSize") String minSize, Model model,
      RedirectAttributes redirectAttributes) {
    String result = ValidationUtills.validateInput(minSize, 0,
        Integer.MAX_VALUE);
    if (!result.equals(VALID)) {
      if (result.equals(NOT_VALID)) {
        result = "input was out of range";
      }
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    int validSize = Integer.parseInt(minSize);
    List<String> groups = groupOptions.findBySize(validSize);
    if (groups.isEmpty()) {
      result = "no groups found";
      model.addAttribute(RESULT, result);
      return ERROR_PAGE;
    }
    redirectAttributes.addFlashAttribute("groups", groups);
    redirectAttributes.addFlashAttribute("minGroupSize", minSize);
    return "redirect:/proceed-group-filter";
  }

  @GetMapping(value = "/proceed-group-filter")
  public String listFilteredGroups() {
    return "filtered-result-page";
  }

}
