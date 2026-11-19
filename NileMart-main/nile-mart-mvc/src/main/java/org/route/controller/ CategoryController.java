package org.route.controller;

import jakarta.validation.Valid;
import org.route.exception.BusinessRuleException;
import org.route.model.Category;
import org.route.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("rows", categoryService.listRows());
        return "categories";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("parents", categoryService.findTopLevel());
        return "categoryForm";
    }

    @PostMapping
    public String save(@Valid @ModelAttribute("category") Category category,
                       BindingResult result,
                       Model model) {
        if (result.hasErrors()) {
            model.addAttribute("parents", categoryService.findTopLevel());
            return "categoryForm";
        }
        try {
            categoryService.create(category);
        } catch (BusinessRuleException e) {
            e.getErrors().forEach((field, message) -> result.rejectValue(field, "rule." + field, message));
            model.addAttribute("parents", categoryService.findTopLevel());
            return "categoryForm";
        }
        return "redirect:/categories";
    }
}
