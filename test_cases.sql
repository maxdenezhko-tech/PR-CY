WITH final_ordered_data AS (
    SELECT
        ts.name as raw_suite_name,
        tc.external_case_id as raw_case_id,
        tc.name as raw_case_name,
        tc.preconditions as raw_preconditions, -- Добавили скрытое поле из базы данных
        tp.step_number,
        tp.step_description,
        tp.expected_result,
        -- Нумеруем строки внутри каждого кейса для скрытия дублей
        row_number() OVER (PARTITION BY tc.id ORDER BY tp.step_number) as step_row_idx
    FROM test_suites ts
    JOIN test_cases tc ON ts.id = tc.suite_id
    JOIN test_steps tp ON tc.id = tp.case_id
)
SELECT
    -- Выводим общую информацию о тесте ТОЛЬКО на первом шаге (idx = 1)
    CASE WHEN step_row_idx = 1 THEN raw_suite_name ELSE NULL END as "Сьют",
    CASE WHEN step_row_idx = 1 THEN raw_case_id ELSE NULL END as "ID Кейса",
    CASE WHEN step_row_idx = 1 THEN raw_case_name ELSE NULL END as "Тест-кейс",
    CASE WHEN step_row_idx = 1 THEN raw_preconditions ELSE NULL END as "Предусловия", -- Новая колонка

    -- Шаги выводим всегда
    step_number as "№ Шага",
    step_description as "Описание шага",
    expected_result as "Ожидаемый результат"
FROM final_ordered_data
ORDER BY raw_suite_name ASC, raw_case_id ASC, step_number ASC;
