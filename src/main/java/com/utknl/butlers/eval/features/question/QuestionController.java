package com.utknl.butlers.eval.features.question;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final QuestionMapper questionMapper;

    @PostMapping("/generate")
    public ResponseEntity<QuestionResponse> generate(@RequestBody @Valid GenerateQuestionRequest request) {
        Question result = questionService.generateQuestion(request);
        return ResponseEntity.ok(questionMapper.toResponse(result));
    }

    @GetMapping("/questions")
    public ResponseEntity<List<QuestionResponse>> getAllQuestions() {
        List<Question> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(questionMapper.toResponseList(questions));
    }

}
