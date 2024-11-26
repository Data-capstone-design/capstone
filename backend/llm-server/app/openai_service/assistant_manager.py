from app.openai_service.assistant_api_utils import create_assistant_model, create_vector_store, \
    add_file_to_vector_store, create_assistant_model_with_vector_store


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
        instructions="You are a exper in IT, computer science field. You read the script of the IT, computer science related vidoe and make list of main topics of the vidoe",
        model="gpt-4o-mini",
        temperature=1.0
    )
    explanation_assistant = await create_assistant_model(
        name="Explanation Model",
        instructions="You are a exper in IT, computer science field. You read the chunked script of the IT, computer science related vidoe and generate explanation",
        model="gpt-4o",
        temperature=0.3
    )

    return AssistantManager(index_assistant=index_assistant, explanation_assistant=explanation_assistant)