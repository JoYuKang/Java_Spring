package com.example.bookmanager.service;

import com.example.bookmanager.domain.Author;
import com.example.bookmanager.domain.Book;
import com.example.bookmanager.repository.AuthorRepository;
import com.example.bookmanager.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    private final EntityManager entityManager;

    private final AuthorService authorService;

    @Transactional(propagation = Propagation.REQUIRED)
    public void putBookAndAuthor(){
        Book book = new Book();
        book.setName("JPA 마스터");

        bookRepository.save(book);

//        Author author = new Author();
//
//        author.setName("J0K");
//
//        authorRepository.save(author);
//
//        authorService.putAuthor();

        try {
            authorService.putAuthor();
        }catch (RuntimeException e){
            e.getMessage();
        }

        throw new RuntimeException("오류 발생 rollback 여부 확인 ");
    }

    @Transactional
    public void get(Long id){
        System.out.println(">>> " + bookRepository.findById(id));
        System.out.println(">>> " + bookRepository.findAll());

        //entityManager.clear();

        System.out.println(">>> " + bookRepository.findById(id));
        System.out.println(">>> " + bookRepository.findAll());

        bookRepository.update();
       // entityManager.clear();

    }

    @Transactional
    public List<Book> getAll(){
        List<Book> books = bookRepository.findAll();

        books.forEach(System.out::println);

        return books;
    }

}
