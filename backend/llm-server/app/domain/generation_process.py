from enum import Enum


class GenerationProcess(Enum):
    OUTLINE_GENERATION = "OutlineGeneration",
    EXPLANATION_GENERATION = "ExplanationGeneration",
    FEEDBACK_GENERATION = "FeedbackGeneration",
    ENHANCED_EXPLANATION_GENERATION = "EnhancedExplanationGeneration",