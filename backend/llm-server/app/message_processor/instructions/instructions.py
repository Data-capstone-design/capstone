def load_prompt(explanation_level, stage, **kwargs):
    prompts = {
        "BASIC": _basic_prompts(),
        "INTERMEDIATE":_intermediate_prompts(),
        "ADVANCED": _advanced_prompts(),
    }
    if explanation_level not in prompts:
        raise ValueError(f"Invalid prompt level: {explanation_level}")
    if stage not in prompts[explanation_level]:
        raise ValueError(f"Invalid prompt stage: {stage}")

    return prompts[explanation_level][stage](**kwargs)

def _basic_prompts():
    return{
        "create_indices": lambda full_original_text=None: (

            "Please read the content below the prompt and identify the \"main\" topics. "
            "For each topic, provide a start timestamp, title, and brief summary in a structured JSON format. "
            "Make sure topics are ordered by start timestamp"
            "Follow this exact structure in your output:\n\n"
            "[{\"start_time\": \"<Start Timestamp>\",\"index\": \"<Topic Title>\",\"summary\": \"<Brief summary of the topic>\"},{\"start_time\": \"<Start Timestamp>\",\"index\": \"<Topic Title>\",\"summary\": \"<Brief summary of the topic>\"},...]"
            "Output should contain only the JSON array with `start_time`, `index`, and `summary` fields for each topic. "
            "Do not add any additional text, explanations, or comments outside of the JSON array."
            
            
            "content start from here:"
            f"{full_original_text}"
            ),
        "create_explanation":lambda chunk_original_text=None: (
            "Please"
            "read the contents under the prompt and generate an explanation suitable for a 6th-grade reading level.Use only the content found in this file to create your response.Do not include any information that is not directly contained in the file."

            "### Explanation Format:"
            "- ** Simple Explanation of the Paragraph **: Provide a brief, simple explanation of the paragraph's main topic or technology."

            "- ** Technical Terms and Definitions **:"
            "- List each IT-related technical term found in the paragraph and provide a simple explanation for each term, suitable for a 6th-grade reading level.Use analogies or relatable examples if necessary to make the term easy to understand."

            "The only output should follow this format and should not include the original file content or any additional text."

            "content start from here:"
            f"{chunk_original_text}"
        ),
        "create_feedback": lambda chunk_original_text=None, chunk_explanation=None: (
            "Please analyze the explanation created above and generate feedback to improve it based on the following criteria:"

            "1. **Simplify the language**: Provide suggestions to simplify the explanation to meet a Flesch-Kincaid grade level of approximately 6. Point out any complex terms or sentences that could be rephrased in simpler language."
            "2. **Ensure IT terminology accuracy**: If any relevant IT terms from the provided vector store appear in the original file (`{request_id}_{chunk_index}.txt`) but are missing in this explanation, include feedback on where to incorporate them to enhance the explanation's accuracy."

            "### Original Text:"
            "Retrieve the original text content from `{request_id}_{chunk_index}.txt` stored in the vector store."

            "### Explanation for Review:"
            "Retrieve the explanation content from `explanation_{request_id}_{chunk_index}.txt` stored in the vector store."

            "### Feedback Format:"
            "- **Language Simplification**: List specific areas in the explanation where the language can be made simpler, with examples if possible."
            "- **IT Terminology Accuracy**: Identify any missing IT terms from the original text that should be included in the explanation, along with suggestions on how to integrate them for clarity."

            "Please provide only the feedback and not an improved explanation."
    ),
        "create_enhanced_explanation": lambda chunk_explanation=None, chunk_feedback=None: (
            "Please improve the created explanation based on the feedback generated about the explanation above."
            "Generate the explanation according to the format of the original explanation."
        )
    }

def _intermediate_prompts():
    return {
        "create_indices": lambda full_original_text=None: (
            "Please read the content below the prompt and identify the main topics. "
            "For each topic, provide a start timestamp, title, and brief summary in a structured JSON format. "
            "Follow this exact structure in your output:\n\n"
            "[{\"start_time\": \"<Start Timestamp>\",\"index\": \"<Topic Title>\",\"summary\": \"<Brief summary of the topic>\"},{\"start_time\": \"<Start Timestamp>\",\"index\": \"<Topic Title>\",\"summary\": \"<Brief summary of the topic>\"},...]"
            "Output should contain only the JSON array with `start_time`, `index`, and `summary` fields for each topic. "
            "Do not add any additional text, explanations, or comments outside of the JSON array."
            "write timestamp with same format in input text"

            "content start from here:"
            f"{full_original_text}"
        ),
        "create_explanation": lambda chunk_original_text=None: (
            "Please"
            "read the contents under the prompt and generate an explanation suitable for a 6th-grade reading level.Use only the content found in this file to create your response.Do not include any information that is not directly contained in the file."

            "### Explanation Format:"
            "- ** Simple Explanation of the Paragraph **: Provide a brief, simple explanation of the paragraph's main topic or technology."

            "- ** Technical Terms and Definitions **:"
            "- List each IT-related technical term found in the paragraph and provide a simple explanation for each term, suitable for a 6th-grade reading level.Use analogies or relatable examples if necessary to make the term easy to understand."

            "The only output should follow this format and should not include the original file content or any additional text."

            "content start from here:"
            f"{chunk_original_text}"
        ),
        "create_feedback": lambda chunk_original_text=None, chunk_explanation=None: (
            "Please analyze the following explanation and generate feedback to improve it based on the following criteria:"

            "1. **Simplify the language**: Provide suggestions to simplify the explanation to meet a Flesch-Kincaid grade level of approximately 6. Point out any complex terms or sentences that could be rephrased in simpler language."
            "2. **Ensure IT terminology accuracy**: If any relevant IT terms from the provided vector store appear in the original file (`{request_id}_{chunk_index}.txt`) but are missing in this explanation, include feedback on where to incorporate them to enhance the explanation's accuracy."

            "### Original Text:"
            "Retrieve the original text content from `{request_id}_{chunk_index}.txt` stored in the vector store."

            "### Explanation for Review:"
            "Retrieve the explanation content from `explanation_{request_id}_{chunk_index}.txt` stored in the vector store."

            "### Feedback Format:"
            "- **Language Simplification**: List specific areas in the explanation where the language can be made simpler, with examples if possible."
            "- **IT Terminology Accuracy**: Identify any missing IT terms from the original text that should be included in the explanation, along with suggestions on how to integrate them for clarity."

            "Please provide only the feedback and not an improved explanation."

            "content start from here : \n\n"
            "original_text : \n\n"
            f"{chunk_original_text} \n\n"
            "explanation : \n\n"
            f"{chunk_explanation}"
        ),
        "create_enhanced_explanation": lambda chunk_explanation=None, chunk_feedback=None: (
            "Please improve the following explanation based on the provided feedback."
            "Generate the explanation according to the format of the original explanation."
            "content start from here:"
            "explanation : \n\n"
            f"{chunk_explanation} \n\n"
            "feedback : \n\n"
            f"{chunk_feedback}"
        )
    }

def _advanced_prompts():
    return {
        "create_indices": lambda full_original_text=None: (
            "Please read the content below the prompt and identify the main topics. "
            "For each topic, provide a start timestamp, title, and brief summary in a structured JSON format. "
            "Follow this exact structure in your output:\n\n"
            "[{\"start_time\": \"<Start Timestamp>\",\"index\": \"<Topic Title>\",\"summary\": \"<Brief summary of the topic>\"},{\"start_time\": \"<Start Timestamp>\",\"index\": \"<Topic Title>\",\"summary\": \"<Brief summary of the topic>\"},...]"
            "Output should contain only the JSON array with `start_time`, `index`, and `summary` fields for each topic. "
            "Do not add any additional text, explanations, or comments outside of the JSON array."
            "write timestamp with same format in input text"

            "content start from here:"
            f"{full_original_text}"
        ),
        "create_explanation": lambda chunk_original_text=None: (
            "Please"
            "read the contents under the prompt and generate an explanation suitable for a 6th-grade reading level.Use only the content found in this file to create your response.Do not include any information that is not directly contained in the file."

            "### Explanation Format:"
            "- ** Simple Explanation of the Paragraph **: Provide a brief, simple explanation of the paragraph's main topic or technology."

            "- ** Technical Terms and Definitions **:"
            "- List each IT-related technical term found in the paragraph and provide a simple explanation for each term, suitable for a 6th-grade reading level.Use analogies or relatable examples if necessary to make the term easy to understand."

            "The only output should follow this format and should not include the original file content or any additional text."

            "content start from here:"
            f"{chunk_original_text}"
        ),
        "create_feedback": lambda chunk_original_text=None, chunk_explanation=None: (
            "Please analyze the following explanation and generate feedback to improve it based on the following criteria:"

            "1. **Simplify the language**: Provide suggestions to simplify the explanation to meet a Flesch-Kincaid grade level of approximately 6. Point out any complex terms or sentences that could be rephrased in simpler language."
            "2. **Ensure IT terminology accuracy**: If any relevant IT terms from the provided vector store appear in the original file (`{request_id}_{chunk_index}.txt`) but are missing in this explanation, include feedback on where to incorporate them to enhance the explanation's accuracy."

            "### Original Text:"
            "Retrieve the original text content from `{request_id}_{chunk_index}.txt` stored in the vector store."

            "### Explanation for Review:"
            "Retrieve the explanation content from `explanation_{request_id}_{chunk_index}.txt` stored in the vector store."

            "### Feedback Format:"
            "- **Language Simplification**: List specific areas in the explanation where the language can be made simpler, with examples if possible."
            "- **IT Terminology Accuracy**: Identify any missing IT terms from the original text that should be included in the explanation, along with suggestions on how to integrate them for clarity."

            "Please provide only the feedback and not an improved explanation."

            "content start from here : \n\n"
            "original_text : \n\n"
            f"{chunk_original_text} \n\n"
            "explanation : \n\n"
            f"{chunk_explanation}"
        ),
        "create_enhanced_explanation": lambda chunk_explanation=None, chunk_feedback=None: (
            "Please improve the following explanation based on the provided feedback."
            "Generate the explanation according to the format of the original explanation."
            "content start from here:"
            "explanation : \n\n"
            f"{chunk_explanation} \n\n"
            "feedback : \n\n"
            f"{chunk_feedback}"
        )
    }
