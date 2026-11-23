package com.swynex.student.controller;

import com.swynex.student.exception.ResourceNotFoundException;
import com.swynex.student.model.Student;
import com.swynex.student.repository.StudentRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentRepository repository;

    public StudentController(StudentRepository repository) {
        this.repository = repository;
    }

    // Public
    @GetMapping
    public List<Student> getAll() {
        return repository.findAll();
    }

    // Public
    @GetMapping("/{id}")
    public Student getById(@PathVariable Long id) {
        return find(id);
    }

    // Protected (X-API-KEY)
    @PostMapping
    public ResponseEntity<Student> create(@Valid @RequestBody Student student) {
        student.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(student));
    }

    // Protected (X-API-KEY)
    @PutMapping("/{id}")
    public Student update(@PathVariable Long id, @Valid @RequestBody Student updated) {
        Student existing = find(id);
        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setAge(updated.getAge());
        existing.setCourse(updated.getCourse());
        return repository.save(existing);
    }

    // Protected (X-API-KEY)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.delete(find(id));
        return ResponseEntity.noContent().build();
    }

    private Student find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id " + id));
    }
}
