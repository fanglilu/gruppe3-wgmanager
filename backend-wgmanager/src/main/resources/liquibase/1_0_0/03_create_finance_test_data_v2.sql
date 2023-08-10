--liquibase formatted sql
--changeset marcello:1.0.1-02

-- Periods
INSERT INTO public.invoice_period (id, created, updated, end_date, invoiced, start_date, wg_id) VALUES ('0e953318-d4c0-40a9-9030-feaa1d14e8ec', '2023-01-27 17:18:58.179837', '2023-01-27 17:18:58.179852', null, false, '2023-01-27 17:18:58.177907', '516bc656-5380-4439-b37c-983135d50733');
INSERT INTO public.invoice_period (id, created, updated, end_date, invoiced, start_date, wg_id) VALUES ('6f729c07-9ca8-49f8-a54b-42f8a50a3855', '2023-01-01 00:17:02.694197', '2023-01-21 00:18:58.181104', '2023-01-21 00:18:58.181104', false, '2023-01-01 00:17:02.694197', '516bc656-5380-4439-b37c-983135d50733');

-- Abos
-- Marcello
INSERT INTO public.abo (id, created, updated, description, payer_id, price, recurring, wg_id) VALUES ('3e90ce5e-73be-4a16-a414-24f6a5f3e185', '2023-01-27 17:18:10.315071', '2023-01-27 17:18:10.315091', 'Internet', 'e3877f42-4a6b-46fc-a573-d7af072f75df', 29.99, 'MONTHLY', '516bc656-5380-4439-b37c-983135d50733');
INSERT INTO public.abo (id, created, updated, description, payer_id, price, recurring, wg_id) VALUES ('d41a6ad7-dc86-4de6-ab7f-5f3338b7e1be', '2023-01-27 17:18:38.965070', '2023-01-27 17:18:38.965088', 'Treppenhausreinigung', 'e3877f42-4a6b-46fc-a573-d7af072f75df', 13.99, 'WEEKLY', '516bc656-5380-4439-b37c-983135d50733');

-- Moritz
INSERT INTO public.abo (id, created, updated, description, payer_id, price, recurring, wg_id) VALUES ('eb3fc256-a6bd-46ad-ac27-1b8f3aa4807d', '2023-01-27 17:47:31.227376', '2023-01-27 17:47:31.227393', 'Strom', '776a40a9-fc43-4fb1-b3fc-95118df25679', 33.56, 'MONTHLY', '516bc656-5380-4439-b37c-983135d50733');

-- Expenses
-- Marcello
INSERT INTO public.expense (id, created, updated, abo_id, description, image, period_id, price, recurring, payer_id) VALUES ('fcc38633-a0e9-401f-a710-e24c2e59cf76', '2023-01-02 17:16:53.475091', '2023-01-02 17:16:53.475110', null, 'Toast', null, '6f729c07-9ca8-49f8-a54b-42f8a50a3855', 1.49, null, 'e3877f42-4a6b-46fc-a573-d7af072f75df');
INSERT INTO public.expense (id, created, updated, abo_id, description, image, period_id, price, recurring, payer_id) VALUES ('be61e16b-4041-4362-a7b0-582417c0b4fa', '2023-01-05 17:17:12.370713', '2023-01-05 17:17:26.086726', null, 'Gewuerze', null, '6f729c07-9ca8-49f8-a54b-42f8a50a3855', 5.79, null, 'e3877f42-4a6b-46fc-a573-d7af072f75df');
INSERT INTO public.expense (id, created, updated, abo_id, description, image, period_id, price, recurring, payer_id) VALUES ('aa0015e8-fd09-4133-be88-1326e8127aef', '2023-01-15 17:17:41.837996', '2023-01-16 17:17:41.838011', null, 'Marmelade', null, '6f729c07-9ca8-49f8-a54b-42f8a50a3855', 1.99, null, 'e3877f42-4a6b-46fc-a573-d7af072f75df');
INSERT INTO public.expense (id, created, updated, abo_id, description, image, period_id, price, recurring, payer_id) VALUES ('97c40e98-08bb-4102-b92e-1b03eeac4277', '2023-01-17 17:17:55.252535', '2023-01-17 17:17:55.252551', null, 'Reiniger', null, '6f729c07-9ca8-49f8-a54b-42f8a50a3855', 7.99, null, 'e3877f42-4a6b-46fc-a573-d7af072f75df');
INSERT INTO public.expense (id, created, updated, abo_id, description, image, period_id, price, recurring, payer_id) VALUES ('61d2a347-c090-4ca9-9926-d225fd7c2eb5', '2023-01-27 17:18:10.322526', '2023-01-27 17:18:10.322542', '3e90ce5e-73be-4a16-a414-24f6a5f3e185', 'Internet', null, '6f729c07-9ca8-49f8-a54b-42f8a50a3855', 29.99, 'MONTHLY', 'e3877f42-4a6b-46fc-a573-d7af072f75df');
INSERT INTO public.expense (id, created, updated, abo_id, description, image, period_id, price, recurring, payer_id) VALUES ('cd1397df-1f89-40af-b661-86848f8c5268', '2023-01-27 17:18:38.971588', '2023-01-27 17:18:38.971604', 'd41a6ad7-dc86-4de6-ab7f-5f3338b7e1be', 'Treppenhausreinigung', null, '6f729c07-9ca8-49f8-a54b-42f8a50a3855', 13.99, 'WEEKLY', 'e3877f42-4a6b-46fc-a573-d7af072f75df');

-- Debts after cash crash
INSERT INTO public.debt (debtor_id, invoice_period_id, receiver_id, debt, settled) VALUES ('776a40a9-fc43-4fb1-b3fc-95118df25679', '6f729c07-9ca8-49f8-a54b-42f8a50a3855', 'e3877f42-4a6b-46fc-a573-d7af072f75df', 20.41, false);
INSERT INTO public.debt (debtor_id, invoice_period_id, receiver_id, debt, settled) VALUES ('66a4df7f-3b2e-4b12-b934-5f42e7e0638e', '6f729c07-9ca8-49f8-a54b-42f8a50a3855', 'e3877f42-4a6b-46fc-a573-d7af072f75df', 20.41, false);


INSERT INTO public.expense (id, created, updated, abo_id, description, image, period_id, price, recurring, payer_id) VALUES ('7d1d6739-85a2-4449-a366-4bf08a461a3f', '2023-01-27 17:19:31.436468', '2023-01-27 17:19:31.436485', null, 'Backpapier', null, '0e953318-d4c0-40a9-9030-feaa1d14e8ec', 1.99, null, 'e3877f42-4a6b-46fc-a573-d7af072f75df');
INSERT INTO public.expense (id, created, updated, abo_id, description, image, period_id, price, recurring, payer_id) VALUES ('09e3d52c-9092-47e0-be47-a17e38fb4b35', '2023-01-27 17:19:42.428257', '2023-01-27 17:19:42.428276', null, 'Butter', null, '0e953318-d4c0-40a9-9030-feaa1d14e8ec', 2.49, null, 'e3877f42-4a6b-46fc-a573-d7af072f75df');
INSERT INTO public.expense (id, created, updated, abo_id, description, image, period_id, price, recurring, payer_id) VALUES ('3b64e25b-da53-4db8-a739-2ec1fe96ddd9', '2023-01-27 17:19:57.519751', '2023-01-27 17:19:57.519770', null, 'Oel', null, '0e953318-d4c0-40a9-9030-feaa1d14e8ec', 5.99, null, 'e3877f42-4a6b-46fc-a573-d7af072f75df');
INSERT INTO public.expense (id, created, updated, abo_id, description, image, period_id, price, recurring, payer_id) VALUES ('2c2734e7-33a9-45ff-b5fd-3a9b53d2e23f', '2023-01-27 17:20:08.820849', '2023-01-27 17:20:08.820866', null, 'Schwaemme', null, '0e953318-d4c0-40a9-9030-feaa1d14e8ec', 2.99, null, 'e3877f42-4a6b-46fc-a573-d7af072f75df');

-- Moritz
INSERT INTO public.expense (id, created, updated, abo_id, description, image, period_id, price, recurring, payer_id) VALUES ('e8bbe85d-0b12-403b-a42a-b3636029e6bc', '2023-01-6 17:47:05.027736', '2023-01-27 17:47:05.027764', null, 'Besen', null, '0e953318-d4c0-40a9-9030-feaa1d14e8ec', 19.99, null, '776a40a9-fc43-4fb1-b3fc-95118df25679');
INSERT INTO public.expense (id, created, updated, abo_id, description, image, period_id, price, recurring, payer_id) VALUES ('5661e1b9-0b6a-4139-a45b-a6d088f0da57', '2023-01-17 17:47:15.820424', '2023-01-27 17:47:15.820441', null, 'Seife', null, '0e953318-d4c0-40a9-9030-feaa1d14e8ec', 1.49, null, '776a40a9-fc43-4fb1-b3fc-95118df25679');
INSERT INTO public.expense (id, created, updated, abo_id, description, image, period_id, price, recurring, payer_id) VALUES ('58845470-9907-421f-b3f2-a7465a9900f0', '2023-01-26 17:47:31.234337', '2023-01-27 17:47:31.234355', 'eb3fc256-a6bd-46ad-ac27-1b8f3aa4807d', 'Strom', null, '0e953318-d4c0-40a9-9030-feaa1d14e8ec', 33.56, 'MONTHLY', '776a40a9-fc43-4fb1-b3fc-95118df25679');
INSERT INTO public.expense (id, created, updated, abo_id, description, image, period_id, price, recurring, payer_id) VALUES ('a4331fb2-9518-4d85-90c9-8159e04850bf', '2023-01-27 17:53:33.371504', '2023-01-27 17:53:33.371521', null, 'Toilettenpapier', null, '0e953318-d4c0-40a9-9030-feaa1d14e8ec', 5.99, null, '776a40a9-fc43-4fb1-b3fc-95118df25679');
INSERT INTO public.expense (id, created, updated, abo_id, description, image, period_id, price, recurring, payer_id) VALUES ('a174632f-a789-4cd5-93d6-93bd0c14624f', '2023-01-27 17:56:06.556344', '2023-01-27 17:56:06.556362', null, 'Stifte', null, '0e953318-d4c0-40a9-9030-feaa1d14e8ec', 3.99, null, '776a40a9-fc43-4fb1-b3fc-95118df25679');
