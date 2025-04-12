CREATE VIEW daily_sales_summary_view
AS
SELECT
        DATE (date_of_sale) AS date_of_sale,
        game_no,
        COUNT (*) AS total_games_sold,
        SUM (sale_price) AS total_sales
        FROM game_sales
        GROUP BY DATE (date_of_sale), game_no;