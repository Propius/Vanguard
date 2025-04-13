-- Combined sales summary view
CREATE VIEW combined_sales_summary_view
AS
SELECT CONCAT(DATE(date_of_sale), '-H', HOUR(date_of_sale)) AS period_of_sale,
       NULL                                                 AS game_no,
       COUNT(*)                                             AS total_games_sold,
       SUM(sale_price)                                      AS total_sales,
       'HOURLY'                                             AS type
FROM game_sales
GROUP BY CONCAT(DATE(date_of_sale), '-H', HOUR(date_of_sale))

UNION ALL

SELECT CONCAT(DATE(date_of_sale), '-H', HOUR(date_of_sale)) AS period_of_sale,
       game_no,
       COUNT(*)                                             AS total_games_sold,
       SUM(sale_price)                                      AS total_sales,
       'HOURLY'                                             AS type
FROM game_sales
GROUP BY CONCAT(DATE(date_of_sale), '-H', HOUR(date_of_sale)), game_no

UNION ALL

SELECT DATE(date_of_sale) AS period_of_sale,
       NULL               AS game_no,
       COUNT(*)           AS total_games_sold,
       SUM(sale_price)    AS total_sales,
       'DAILY'            AS type
FROM game_sales
GROUP BY DATE(date_of_sale)

UNION ALL

SELECT DATE(date_of_sale) AS period_of_sale,
       game_no,
       COUNT(*)           AS total_games_sold,
       SUM(sale_price)    AS total_sales,
       'DAILY'            AS type
FROM game_sales
GROUP BY DATE(date_of_sale), game_no

UNION ALL

SELECT CONCAT(YEAR(date_of_sale), '-W', WEEK(date_of_sale)) AS period_of_sale,
       NULL                                                 AS game_no,
       COUNT(*)                                             AS total_games_sold,
       SUM(sale_price)                                      AS total_sales,
       'WEEKLY'                                             AS type
FROM game_sales
GROUP BY CONCAT(YEAR(date_of_sale), '-W', WEEK(date_of_sale))

UNION ALL

SELECT CONCAT(YEAR(date_of_sale), '-W', WEEK(date_of_sale)) AS period_of_sale,
       game_no,
       COUNT(*)                                             AS total_games_sold,
       SUM(sale_price)                                      AS total_sales,
       'WEEKLY'                                             AS type
FROM game_sales
GROUP BY CONCAT(YEAR(date_of_sale), '-W', WEEK(date_of_sale)), game_no

UNION ALL

SELECT DATE_FORMAT(date_of_sale, '%Y-%m') AS period_of_sale,
       NULL                               AS game_no,
       COUNT(*)                           AS total_games_sold,
       SUM(sale_price)                    AS total_sales,
       'MONTHLY'                          AS type
FROM game_sales
GROUP BY DATE_FORMAT(date_of_sale, '%Y-%m')

UNION ALL

SELECT DATE_FORMAT(date_of_sale, '%Y-%m') AS period_of_sale,
       game_no,
       COUNT(*)                           AS total_games_sold,
       SUM(sale_price)                    AS total_sales,
       'MONTHLY'                          AS type
FROM game_sales
GROUP BY DATE_FORMAT(date_of_sale, '%Y-%m'), game_no

UNION ALL

SELECT CONCAT(YEAR(date_of_sale), '-Q', QUARTER(date_of_sale)) AS period_of_sale,
       NULL                                                    AS game_no,
       COUNT(*)                                                AS total_games_sold,
       SUM(sale_price)                                         AS total_sales,
       'QUARTERLY'                                             AS type
FROM game_sales
GROUP BY CONCAT(YEAR(date_of_sale), '-Q', QUARTER(date_of_sale))

UNION ALL

SELECT CONCAT(YEAR(date_of_sale), '-Q', QUARTER(date_of_sale)) AS period_of_sale,
       game_no,
       COUNT(*)                                                AS total_games_sold,
       SUM(sale_price)                                         AS total_sales,
       'QUARTERLY'                                             AS type
FROM game_sales
GROUP BY CONCAT(YEAR(date_of_sale), '-Q', QUARTER(date_of_sale)), game_no

UNION ALL

SELECT DATE_FORMAT(date_of_sale, '%Y') AS period_of_sale,
       NULL                            AS game_no,
       COUNT(*)                        AS total_games_sold,
       SUM(sale_price)                 AS total_sales,
       'YEARLY'                        AS type
FROM game_sales
GROUP BY DATE_FORMAT(date_of_sale, '%Y')

UNION ALL

SELECT DATE_FORMAT(date_of_sale, '%Y') AS period_of_sale,
       game_no,
       COUNT(*)                        AS total_games_sold,
       SUM(sale_price)                 AS total_sales,
       'YEARLY'                        AS type
FROM game_sales
GROUP BY DATE_FORMAT(date_of_sale, '%Y'), game_no;
