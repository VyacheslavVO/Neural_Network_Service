-- Необходимо проверять ограничения внешнего ключа (это значение по умолчанию)
SET FOREIGN_KEY_CHECKS =  1;

-- Установка значений сервера по умолчанию
INSERT INTO neural_network.tcp_server (name, port, role_response)
SELECT 'tcp server', 3345, 'broadcast' WHERE NOT EXISTS (
		SELECT 1 FROM neural_network.tcp_server WHERE name = 'tcp server' AND port = 3345 AND role_response = 'broadcast'
);







