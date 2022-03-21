package mk.ukim.finki.emt.eshop.web.rest;

import mk.ukim.finki.emt.eshop.model.Category;
import mk.ukim.finki.emt.eshop.service.CategoryService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "https://fcse-emt-frontend.herokuapp.com")
@RequestMapping("/api/categories")
public class CategoryRestController {

    private final CategoryService categoryService;

    public CategoryRestController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<Category> findAll() {
        return this.categoryService.listCategories();
    }
}
