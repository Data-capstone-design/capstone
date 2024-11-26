import time

from app.grafana_watcher.grafana_handler import __send_metrics, __send_logs, __calculate_cost, __check


async def monitor(response, metrics_url, logs_url, metrics_username, logs_username, access_token, disable_content=False, environment="default"):
    metrics_url, logs_url = __check(metrics_url,
                                    logs_url,
                                    metrics_username,
                                    logs_username,
                                    access_token
                            )

    response = response
    start_time = response.created_at
    end_time = response.completed_at
    duration = end_time - start_time

    model = response.model

    # Calculate the cost based on the response's usage
    cost = __calculate_cost(model,
                            response.usage.prompt_tokens,
                            response.usage.completion_tokens
            )

    # Prepare logs to be sent
    logs = {
        "streams": [
        {
                "stream": {
                    "job": "integrations/openai",
                    "model": response.model,
                    "finish_reason": response.status,
                    "prompt_tokens": str(response.usage.prompt_tokens),
                    "completion_tokens": str(response.usage.completion_tokens),
                    "total_tokens": str(response.usage.total_tokens)
                },
                "values": [
                    [
                        str(int(time.time()) * 1000000000),
                    ]
                ]

            }
        ]
    }
    if disable_content is True:
        # Send logs to the specified logs URL
        __send_logs(logs_url=logs_url,
                    logs_username=logs_username,
                    access_token=access_token,
                    logs=logs
        )

    # Prepare metrics to be sent
    metrics = [
        # Metric to track the number of completion tokens used in the response
        f'openai,job=integrations/openai,'
        f'source=python_custom_assistantv1,model={response.model},environment={environment} '
        f'completionTokens={response.usage.completion_tokens}',

        # Metric to track the number of prompt tokens used in the response
        f'openai,job=integrations/openai,'
        f'source=python_custom_assistantv1,model={response.model},environment={environment} '
        f'promptTokens={response.usage.prompt_tokens}',

        # Metric to track the total number of tokens used in the response
        f'openai,job=integrations/openai,'
        f'source=python_custom_assistantv1,model={response.model},environment={environment} '
        f'totalTokens={response.usage.total_tokens}',

        # Metric to track the usage cost based on the model and token usage
        f'openai,job=integrations/openai,'
        f'source=python_custom_assistantv1,model={response.model},environment={environment} '
        f'usageCost={cost}',

        # Metric to track the duration of the API request and response cycle
        f'openai,job=integrations/openai,'
        f'source=python_custom_assistantv1,model={response.model},environment={environment} '
        f'requestDuration={duration}',
    ]

    # Send metrics to the specified metrics URL
    __send_metrics(metrics_url=metrics_url,
                   metrics_username=metrics_username,
                   access_token=access_token,
                   metrics=metrics)