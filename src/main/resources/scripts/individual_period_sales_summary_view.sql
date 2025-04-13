-- Hourly sales summary view
CREATE VIEW hourly_sales_summary_view
AS
SELECT CONCAT(DATE(date_of_sale), '-H', HOUR(date_of_sale)) AS date_of_sale,
       NULL                                                 AS game_no,
       COUNT(*)                                             AS total_games_sold,
       SUM(sale_price)                                      AS total_sales
FROM game_sales
GROUP BY CONCAT(DATE(date_of_sale), '-H', HOUR(date_of_sale));

-- Hourly sales summary per game view
CREATE VIEW hourly_sales_summary_per_game_view
AS
SELECT CONCAT(DATE(date_of_sale), '-H', HOUR(date_of_sale)) AS date_of_sale,
       game_no,
       COUNT(*)                                             AS total_games_sold,
       SUM(sale_price)                                      AS total_sales
FROM game_sales
GROUP BY CONCAT(DATE(date_of_sale), '-H', HOUR(date_of_sale)), game_no;

-- Weekly sales summary view
CREATE VIEW weekly_sales_summary_view
AS
SELECT
        YEAR (date_of_sale) AS year_of_sale,
        WEEK(date_of_sale) AS week_of_sale,
        NULL AS game_no,
        COUNT (*) AS total_games_sold,
        SUM (sale_price) AS total_sales
        FROM game_sales
        GROUP BY YEAR (date_of_sale), WEEK(date_of_sale);

-- Weekly sales summary per game view
CREATE VIEW weekly_sales_summary_per_game_view
AS
SELECT
        YEAR (date_of_sale) AS year_of_sale,
        WEEK(date_of_sale) AS week_of_sale,
        game_no,
        COUNT (*) AS total_games_sold,
        SUM (sale_price) AS total_sales
        FROM game_sales
        GROUP BY YEAR (date_of_sale), WEEK(date_of_sale), game_no;

-- Daily sales summary view
CREATE VIEW daily_sales_summary_view
AS
SELECT
        DATE (date_of_sale) AS date_of_sale,
        NULL AS game_no,
        COUNT (*) AS total_games_sold,
        SUM (sale_price) AS total_sales
        FROM game_sales
        GROUP BY DATE (date_of_sale);

-- Daily sales summary per game view
CREATE VIEW daily_sales_summary_per_game_view
AS
SELECT
        DATE (date_of_sale) AS date_of_sale,
        game_no,
        COUNT (*) AS total_games_sold,
        SUM (sale_price) AS total_sales
        FROM game_sales
        GROUP BY DATE (date_of_sale), game_no;

-- Monthly sales summary view
CREATE VIEW monthly_sales_summary_view
AS
SELECT DATE_FORMAT(date_of_sale, '%Y-%m') AS month_of_sale,
       NULL                               AS game_no,
       COUNT(*)                           AS total_games_sold,
       SUM(sale_price)                    AS total_sales
FROM game_sales
GROUP BY DATE_FORMAT(date_of_sale, '%Y-%m');

-- Monthly sales summary per game view
CREATE VIEW monthly_sales_summary_per_game_view
AS
SELECT DATE_FORMAT(date_of_sale, '%Y-%m') AS month_of_sale,
       game_no,
       COUNT(*)                           AS total_games_sold,
       SUM(sale_price)                    AS total_sales
FROM game_sales
GROUP BY DATE_FORMAT(date_of_sale, '%Y-%m'), game_no;

-- Quarterly sales summary view
CREATE VIEW quarterly_sales_summary_view
AS
SELECT CONCAT(YEAR(date_of_sale), '-Q', QUARTER(date_of_sale)) AS quarter_of_sale,
       NULL                                                    AS game_no,
       COUNT(*)                                                AS total_games_sold,
       SUM(sale_price)                                         AS total_sales
FROM game_sales
GROUP BY CONCAT(YEAR(date_of_sale), '-Q', QUARTER(date_of_sale));

-- Quarterly sales summary per game view
CREATE VIEW quarterly_sales_summary_per_game_view
AS
SELECT CONCAT(YEAR(date_of_sale), '-Q', QUARTER(date_of_sale)) AS quarter_of_sale,
       game_no,
       COUNT(*)                                                AS total_games_sold,
       SUM(sale_price)                                         AS total_sales
FROM game_sales
GROUP BY CONCAT(YEAR(date_of_sale), '-Q', QUARTER(date_of_sale)), game_no;

-- Yearly sales summary view
CREATE VIEW yearly_sales_summary_view
AS
SELECT DATE_FORMAT(date_of_sale, '%Y') AS year_of_sale,
       NULL                            AS game_no,
       COUNT(*)                        AS total_games_sold,
       SUM(sale_price)                 AS total_sales
FROM game_sales
GROUP BY DATE_FORMAT(date_of_sale, '%Y');

-- Yearly sales summary per game view
CREATE VIEW yearly_sales_summary_per_game_view
AS
SELECT DATE_FORMAT(date_of_sale, '%Y') AS year_of_sale,
       game_no,
       COUNT(*)                        AS total_games_sold,
       SUM(sale_price)                 AS total_sales
FROM game_sales
GROUP BY DATE_FORMAT(date_of_sale, '%Y'), game_no;
