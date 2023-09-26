delete ci
    from cart_items ci
    left join books b on ci.book_id = b.id
where b.id = 4;