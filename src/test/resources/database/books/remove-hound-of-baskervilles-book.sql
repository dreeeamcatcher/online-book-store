delete bc
    from books_categories bc
    left join books b
    on bc.book_id = b.id
where b.title = 'The Hound of the Baskervilles';

delete from books where title = 'The Hound of the Baskervilles';
