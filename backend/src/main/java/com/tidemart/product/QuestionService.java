package com.tidemart.product;

import com.tidemart.common.exception.BadRequestException;
import com.tidemart.common.exception.ResourceNotFoundException;
import com.tidemart.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    public record Answer(String user, String answer) {}
    public record QA(Long id, String user, String question, String createdAt, List<Answer> answers) {}
    private final ProductQuestionRepository questions;
    private final ProductAnswerRepository answers;
    private final UserRepository users;

    public QuestionService(ProductQuestionRepository questions, ProductAnswerRepository answers, UserRepository users) { this.questions = questions; this.answers = answers; this.users = users; }

    public List<QA> list(Long productId) {
        return questions.findByProductIdOrderByIdDesc(productId).stream().map(q -> new QA(q.id, name(q.userId), q.question, String.valueOf(q.createdAt).substring(0, 10),
                answers.findByQuestionIdOrderByIdAsc(q.id).stream().map(a -> new Answer(name(a.userId), a.answer)).toList())).toList();
    }

    public void ask(Long uid, Long productId, String text) {
        if (text == null || text.isBlank()) throw new BadRequestException("Write your question");
        ProductQuestion q = new ProductQuestion();
        q.productId = productId; q.userId = uid; q.question = text.trim();
        questions.save(q);
    }

    public void answer(Long uid, Long questionId, String text) {
        if (text == null || text.isBlank()) throw new BadRequestException("Write your answer");
        if (!questions.existsById(questionId)) throw new ResourceNotFoundException("Question not found");
        ProductAnswer a = new ProductAnswer();
        a.questionId = questionId; a.userId = uid; a.answer = text.trim();
        answers.save(a);
    }

    private String name(Long uid) { return users.findById(uid).map(u -> u.name == null ? "Customer" : u.name).orElse("Customer"); }
}
