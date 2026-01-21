package com.utknl.butlers.eval.features.question;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final QuestionMapper questionMapper;

    @GetMapping("/generate")
    public ResponseEntity<QuestionResponse> generate() {
        Question result = questionService.generateQuestion();
        return ResponseEntity.ok(questionMapper.toResponse(result));
    }

    @GetMapping("/questions")
    public ResponseEntity<List<QuestionResponse>> getAllQuestions() {
        List<Question> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(questionMapper.toResponseList(questions));
    }

}
