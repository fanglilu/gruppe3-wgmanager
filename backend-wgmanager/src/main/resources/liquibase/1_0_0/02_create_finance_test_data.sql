--liquibase formatted sql
--changeset fangli lu:1.0.0-02

INSERT INTO expense (id, created, updated, abo_id, description, image, period_id, price, recurring, payer_id)
VALUES ('dbbd171b-ff93-4481-b464-93b8c29e7d6f', '2023-01-08 10:41:37.903', '2023-01-08 10:41:37.903', NULL, 'Obst',
        NULL, '2c5efd33-3598-488c-9eb2-e5a76249dd54', 7.77, NULL, '2469ec11-28ef-4031-9e5d-9a811074f161'),
       ('fff79ae8-dfbf-4b4d-9edd-af344a054171', '2023-01-12 14:41:37.903', '2023-01-12 14:41:37.903', NULL,
        'Verschiedene Pasta', NULL, '2c5efd33-3598-488c-9eb2-e5a76249dd54', 13.80, NULL,
        '2469ec11-28ef-4031-9e5d-9a811074f161'),
       ('345fd3b7-8372-42be-bf4f-a579646e659c', '2023-01-23 17:41:37.903', '2023-01-23 17:41:37.903', NULL,
        'Geschenkpapier', NULL, '2c5efd33-3598-488c-9eb2-e5a76249dd54', 2.10, NULL,
        '2469ec11-28ef-4031-9e5d-9a811074f161')