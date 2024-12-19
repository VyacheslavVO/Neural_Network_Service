-- Не проверять ограничения внешнего ключа
SET FOREIGN_KEY_CHECKS =  0;

-- Таблица настроек сервера
CREATE TABLE IF NOT EXISTS tcp_server (
                    id INT(11) NOT NULL AUTO_INCREMENT,
                    name                  VARCHAR(50) NOT NULL,
                    port                  INT(11) NOT NULL,
                        PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

-- Таблица сцен нейронной сети
CREATE TABLE IF NOT EXISTS n_network (
                    id                    INT(11) NOT NULL AUTO_INCREMENT,
                    scene_name                  VARCHAR(50) NOT NULL,
                    summ_in_layer               INT(11) NOT NULL,
                    summ_out_layer              INT(11) NOT NULL,
                        PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;