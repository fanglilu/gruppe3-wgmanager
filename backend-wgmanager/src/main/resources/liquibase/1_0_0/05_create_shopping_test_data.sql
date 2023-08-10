--liquibase formatted sql
--changeset niklas:1.0.0-05

-- Lists
--- 'WG Bärenbad'
INSERT INTO public.lists (id, created, updated, creator, name, num_items, private, value, wgid) VALUES ('b293ce14-a466-11ed-a8fc-0242ac120002', '2023-02-04 17:18:58.179837', null, '2469ec11-28ef-4031-9e5d-9a811074f161', 'WG Weekly Shopping', 2, false, 0, '30f81789-5f48-47a3-9281-17601b65adc8');
INSERT INTO public.lists (id, created, updated, creator, name, num_items, private, value, wgid) VALUES ('bde02b8c-a466-11ed-a8fc-0242ac120002', '2023-01-01 00:17:02.694197', null, '7b9d869b-99f8-45b0-9018-9af60fe8e475', 'Bad renovieren', 5, false, 0, '30f81789-5f48-47a3-9281-17601b65adc8');

--- 'WG MoMaNi'
INSERT INTO public.lists (id, created, updated, creator, name, num_items, private, value, wgid) VALUES ('fb554d57-ee39-49a8-a2b2-6e9b179ed5c6', '2023-01-10 17:18:58.179837', null, '776a40a9-fc43-4fb1-b3fc-95118df25679', 'Party am 10.02.', 4, false, 0, '516bc656-5380-4439-b37c-983135d50733');
INSERT INTO public.lists (id, created, updated, creator, name, num_items, private, value, wgid) VALUES ('a12130bd-9601-4a48-a63f-79f851dd7122', '2023-02-01 00:17:02.694197', null, '66a4df7f-3b2e-4b12-b934-5f42e7e0638e', 'MoNaNi', 2,  false, 0, '516bc656-5380-4439-b37c-983135d50733');
INSERT INTO public.lists (id, created, updated, creator, name, num_items, private, value, wgid) VALUES ('e774fab2-0e5e-468a-b515-7481bcb5e896', '2023-02-04 17:18:58.179837', null, 'e3877f42-4a6b-46fc-a573-d7af072f75df', 'Getränke', 3, false, 0, '516bc656-5380-4439-b37c-983135d50733');
INSERT INTO public.lists (id, created, updated, creator, name, num_items, private, value, wgid) VALUES ('66576fda-2c60-45f9-81bd-1c371a852400', '2022-12-24 00:17:02.694197', null, 'e3877f42-4a6b-46fc-a573-d7af072f75df', 'Last minute Weihnachtsschopping', 0, true, 0, '516bc656-5380-4439-b37c-983135d50733');

--- 'WG Ausland'
INSERT INTO public.lists (id, created, updated, creator, name, num_items, private, value, wgid) VALUES ('86714c81-90d8-4582-9ff8-236664545525', '2023-01-25 17:18:58.179837', null, '66a4df7f-3b2e-4b12-b934-5f42e7e0638e', '55 Rue du Faubourg Saint-Honoré', 8, false, 0, 'ab0c6de8-1298-42b4-b9e3-e3dcafe80829');
INSERT INTO public.lists (id, created, updated, creator, name, num_items, private, value, wgid) VALUES ('c801ff86-6257-4d91-8b37-06b6605f10f1', '2023-01-22 00:17:02.694197', null, '66a4df7f-3b2e-4b12-b934-5f42e7e0638e', 'Meine eigene Liste', 1, true, 0, 'ab0c6de8-1298-42b4-b9e3-e3dcafe80829');
INSERT INTO public.lists (id, created, updated, creator, name, num_items, private, value, wgid) VALUES ('0f0d2d4c-5910-46d6-b26a-cc07c5584182', '2023-01-30 17:18:58.179837', null, '56fbdf94-922d-4ceb-8575-4268aa421025', 'Liste pour monop', 0, false, 0, 'ab0c6de8-1298-42b4-b9e3-e3dcafe80829');
INSERT INTO public.lists (id, created, updated, creator, name, num_items, private, value, wgid) VALUES ('8669e9bc-8338-44b5-83b3-82b070b471f4', '2023-01-10 17:18:58.179837', null, '56fbdf94-922d-4ceb-8575-4268aa421025', 'Ideés pour l anniversaire', 0, true, 0, 'ab0c6de8-1298-42b4-b9e3-e3dcafe80829');

--items
--- 'WG Bärenbad'
---- Liste 1
INSERT INTO public.items (id, created, updated, description, is_bought, listid, name, owner) VALUES ('05e2d9ac-4423-4eef-abaa-1516081b3382', '2023-02-04 17:18:58.179837', null, 'Extra weich', false, 'b293ce14-a466-11ed-a8fc-0242ac120002', 'Klopapier', '2469ec11-28ef-4031-9e5d-9a811074f161');
INSERT INTO public.items (id, created, updated, description, is_bought, listid, name, owner) VALUES ('136f6ddf-41b9-4745-bd07-ed6bda42cd5c', '2023-02-04 17:18:58.179837', null, 'Breaburn', false, 'b293ce14-a466-11ed-a8fc-0242ac120002', 'Äpfel', '7b9d869b-99f8-45b0-9018-9af60fe8e475');

---- Liste 2
INSERT INTO public.items (id, created, updated, description, is_bought, listid, name, owner) VALUES ('9717c262-9f3f-4e04-b7af-fd0c84462203', '2023-01-01 17:18:58.179837', null, 'Grau', false, 'bde02b8c-a466-11ed-a8fc-0242ac120002', 'Zement', '7b9d869b-99f8-45b0-9018-9af60fe8e475');
INSERT INTO public.items (id, created, updated, description, is_bought, listid, name, owner) VALUES ('232a5c26-1f5e-4396-a934-6da21953638f', '2023-01-01 17:18:58.179837', null, 'Ohne Muster', true, 'bde02b8c-a466-11ed-a8fc-0242ac120002', 'Fliesen', '7b9d869b-99f8-45b0-9018-9af60fe8e475');
INSERT INTO public.items (id, created, updated, description, is_bought, listid, name, owner) VALUES ('065adf8b-8f66-4cf0-8071-6647fac34508', '2023-01-01 17:18:58.179837', null, 'Stein', false, 'bde02b8c-a466-11ed-a8fc-0242ac120002', 'Waschbecken', '7b9d869b-99f8-45b0-9018-9af60fe8e475');
INSERT INTO public.items (id, created, updated, description, is_bought, listid, name, owner) VALUES ('c355351a-1ea8-496b-a66e-ae86be73be97', '2023-01-01 17:18:58.179837', null, 'Weiß', false, 'bde02b8c-a466-11ed-a8fc-0242ac120002', 'Farbe', '7b9d869b-99f8-45b0-9018-9af60fe8e475');
INSERT INTO public.items (id, created, updated, description, is_bought, listid, name, owner) VALUES ('18c37227-4ba8-4433-8d78-11a701e3f8ed', '2023-01-01 17:18:58.179837', null, 'Grau', false, 'bde02b8c-a466-11ed-a8fc-0242ac120002', 'Teppich', '7b9d869b-99f8-45b0-9018-9af60fe8e475');

--- 'WG MoMaNi'
---- Liste 1
INSERT INTO public.items (id, created, updated, description, is_bought, listid, name, owner) VALUES ('c368aaac-812b-4924-b17b-6a450d400d86', '2023-01-10 17:18:58.179837', null, 'rot', false, 'fb554d57-ee39-49a8-a2b2-6e9b179ed5c6', 'Becher', '66a4df7f-3b2e-4b12-b934-5f42e7e0638e');
INSERT INTO public.items (id, created, updated, description, is_bought, listid, name, owner) VALUES ('6953446a-f1af-4d13-a189-b0dab70a19b9', '2023-01-10 17:18:58.179837', null, '10 Liter', false, 'fb554d57-ee39-49a8-a2b2-6e9b179ed5c6', 'O-Saft', '66a4df7f-3b2e-4b12-b934-5f42e7e0638e');
INSERT INTO public.items (id, created, updated, description, is_bought, listid, name, owner) VALUES ('babc3d06-acc8-4bb9-bec4-550bbcac2a2a', '2023-01-10 17:18:58.179837', null, 'Bunt!', false, 'fb554d57-ee39-49a8-a2b2-6e9b179ed5c6', 'Girlanden', '776a40a9-fc43-4fb1-b3fc-95118df25679');
INSERT INTO public.items (id, created, updated, description, is_bought, listid, name, owner) VALUES ('20a49878-f8af-4e42-9f3f-b9171893efb8', '2023-01-10 17:18:58.179837', null, 'Saftig', false, 'fb554d57-ee39-49a8-a2b2-6e9b179ed5c6', 'Kuchen', '776a40a9-fc43-4fb1-b3fc-95118df25679');

---- Liste 2
INSERT INTO public.items (id, created, updated, description, is_bought, listid, name, owner) VALUES ('b04ebf87-9bc2-40c4-a320-2be402dbc364', '2023-02-01 17:18:58.179837', null, 'Griechisch', false, 'a12130bd-9601-4a48-a63f-79f851dd7122', 'Joghurt', 'e3877f42-4a6b-46fc-a573-d7af072f75df');
INSERT INTO public.items (id, created, updated, description, is_bought, listid, name, owner) VALUES ('3dbef6d8-aee4-4572-af49-c598ba7141d4', '2023-02-01 17:18:58.179837', null, 'Laktosefrei', false, 'a12130bd-9601-4a48-a63f-79f851dd7122', 'Feta', '66a4df7f-3b2e-4b12-b934-5f42e7e0638e');

---- Liste 3
INSERT INTO public.items (id, created, updated, description, is_bought, listid, name, owner) VALUES ('febd8f01-4271-4c1e-a48e-d54cdfa3a3b8', '2023-02-04 17:18:58.179837', null, 'Grapefruit', true, 'e774fab2-0e5e-468a-b515-7481bcb5e896', 'Schwepps', 'e3877f42-4a6b-46fc-a573-d7af072f75df');
INSERT INTO public.items (id, created, updated, description, is_bought, listid, name, owner) VALUES ('d4318e41-8402-4cca-bdfa-6298b1135c1c', '2023-02-04 17:18:58.179837', null, 'Paulaner', true, 'e774fab2-0e5e-468a-b515-7481bcb5e896', 'Spezi', 'e3877f42-4a6b-46fc-a573-d7af072f75df');
INSERT INTO public.items (id, created, updated, description, is_bought, listid, name, owner) VALUES ('fb5a1fbf-6f33-44e1-92af-db62ed4871f6', '2023-02-04 17:18:58.179837', null, 'Lecker', false, 'e774fab2-0e5e-468a-b515-7481bcb5e896', 'Saft', 'e3877f42-4a6b-46fc-a573-d7af072f75df');


--- 'WG Ausland'
---- Liste 1
INSERT INTO public.items (id, created, updated, description, is_bought, listid, name, owner) VALUES ('6a5d0037-6814-460d-bf3c-efdca1a66b5c', '2023-01-25 17:18:58.179837', null, 'Pomme', false, '86714c81-90d8-4582-9ff8-236664545525', 'Jus', '56fbdf94-922d-4ceb-8575-4268aa421025');
INSERT INTO public.items (id, created, updated, description, is_bought, listid, name, owner) VALUES ('9cc97c6d-62ca-4a6b-889b-70c2b13110d7', '2023-01-25 17:18:58.179837', null, 'Côte de Provence ou Bandol', false, '86714c81-90d8-4582-9ff8-236664545525', 'Vin rosé', '56fbdf94-922d-4ceb-8575-4268aa421025');
INSERT INTO public.items (id, created, updated, description, is_bought, listid, name, owner) VALUES ('18189dc5-d7b6-4f68-8758-9505a4382867', '2023-01-25 17:18:58.179837', null, '', false, '86714c81-90d8-4582-9ff8-236664545525', 'Twix', '56fbdf94-922d-4ceb-8575-4268aa421025');
INSERT INTO public.items (id, created, updated, description, is_bought, listid, name, owner) VALUES ('c3668eb6-7da6-4501-8332-df4a970025e9', '2023-01-25 17:18:58.179837', null, '25 mois', false, '86714c81-90d8-4582-9ff8-236664545525', 'Comté', '56fbdf94-922d-4ceb-8575-4268aa421025');
INSERT INTO public.items (id, created, updated, description, is_bought, listid, name, owner) VALUES ('15e1e499-78e4-4a36-8e90-306abf7f2f1f', '2023-01-25 17:18:58.179837', null, 'Pas trop cuire', false, '86714c81-90d8-4582-9ff8-236664545525', 'Pain', '56fbdf94-922d-4ceb-8575-4268aa421025');
INSERT INTO public.items (id, created, updated, description, is_bought, listid, name, owner) VALUES ('67e0d425-d6a0-41ef-ab0b-595bed661b24', '2023-01-25 17:18:58.179837', null, 'Garnier', false, '86714c81-90d8-4582-9ff8-236664545525', 'Shampoo', '56fbdf94-922d-4ceb-8575-4268aa421025');
INSERT INTO public.items (id, created, updated, description, is_bought, listid, name, owner) VALUES ('1d1f2e6e-c111-48af-bda3-9b4236936924', '2023-01-25 17:18:58.179837', null, '3', false, '86714c81-90d8-4582-9ff8-236664545525', 'Zucchini', '66a4df7f-3b2e-4b12-b934-5f42e7e0638e');
INSERT INTO public.items (id, created, updated, description, is_bought, listid, name, owner) VALUES ('4a83d29c-8342-4352-acf0-14e17ed1d57d', '2023-01-25 17:18:58.179837', null, '1 sachet', false, '86714c81-90d8-4582-9ff8-236664545525', 'Framboise', '66a4df7f-3b2e-4b12-b934-5f42e7e0638e');

---- Liste 2
INSERT INTO public.items (id, created, updated, description, is_bought, listid, name, owner) VALUES ('54e875cf-759a-4827-b327-f9b837b479ea', '2023-01-22 17:18:58.179837', null, 'Soft Cookies', true, 'c801ff86-6257-4d91-8b37-06b6605f10f1', 'Kekse', '66a4df7f-3b2e-4b12-b934-5f42e7e0638e');