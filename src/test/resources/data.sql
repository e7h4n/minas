SET mode MySQL;

INSERT INTO `crm_room` (id, region, building, unit, house_number, room_area, parking_space_area)
  VALUES
  (1, 1, 1, 1, 1, 1.0, 1.0),
  (2, 2, 2, 2, 2, 2.0, 2.0);

INSERT INTO `crm_resident` (id, `name`, mobile_phone, telephone, sex, address, room_id, wechat_user_id, verified, vote_id)
  VALUES
  (1, 'foo', '1', '1', 1, 'fdjskafdsa', 1, 1, 1, 1),
  (2, 'bar', '1', '1', 1, 'fdjskafdsa', 1, 2, 1, 2);