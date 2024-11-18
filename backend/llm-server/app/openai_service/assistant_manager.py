from app.openai_service.assistant_api_utils import create_assistant_model

class AssistantManager:
    def __init__(self, index_assistant, explanation_assistant):
        self.index_assistant = index_assistant
        self.explanation_assistant = explanation_assistant

async def initialize_assistants():
    """
    Assistant 모델 초기화 및 반환.
    """
    index_assistant = await create_assistant_model(
        name="Index Model",
        instructions="Index Instructions",
        model="gpt-4o-mini"
    )
    explanation_assistant = await create_assistant_model(
        name="Explanation Model",
        instructions="Explanation Instructions",
        model="gpt-4o"
    )

    return AssistantManager(index_assistant=index_assistant, explanation_assistant=explanation_assistant)