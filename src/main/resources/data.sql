INSERT INTO employee_category (id, name) VALUES
('11111111-1111-1111-1111-111111111111', 'Разработчики');

INSERT INTO employee_category (id, name) VALUES
('22222222-2222-2222-2222-222222222222', 'Менеджеры');

INSERT INTO employee (id, full_name, category_id) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Иван Иванов', '11111111-1111-1111-1111-111111111111');

INSERT INTO employee (id, full_name, category_id) VALUES
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Анна Петрова', '22222222-2222-2222-2222-222222222222');


INSERT INTO employee (id, full_name, category_id) VALUES
('cccccccc-cccc-cccc-cccc-cccccccccccc', 'Иван Денисов', '22222222-2222-2222-2222-222222222222');


INSERT INTO employee_characteristics (employee_id, characteristics) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Java');

INSERT INTO employee_characteristics (employee_id, characteristics) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Spring');


INSERT INTO employee_characteristics (employee_id, characteristics) VALUES
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'Spring');

