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
            """
            Please analyze the following script and identify **very broad, main topics**.

            1. Focus on dividing the script into logical sections based on **major themes or topics**.
            2. Avoid creating overly detailed or granular sections. Combine related content into **larger, meaningful units** to form cohesive topics.
            3. Each topic should represent a **complete logical unit or core concept** and provide sufficient context for understanding.
            4. Aim to identify **5 to 9topics** per 30minute of text. Adjust the granularity to ensure the number of topics remains within this range.

            For each topic, provide a start timestamp, title(in english), and short and clear summary(in korean) in a structured JSON format. 
            Make sure topics are ordered by start timestamp
            Follow this exact structure in your output:\n\n
            [{\"startTime\": \"<Start Timestamp>\",\"title\": \"<Topic Title>\",\"summary\": \"<Brief summary of the topic>\"},{\"startTime\": \"<Start Timestamp>\",\"index\": \"<Topic Title>\",\"summary\": \"<Brief summary of the topic>\"},...]
            Output should contain only the JSON array with `start_time`, `index`, and `summary` fields for each topic. 
            Do not add any additional text, explanations, or comments outside of the JSON array.
            double check to make sure summary is in korean and title is english.
    
            content start from here:"""
            f"{full_original_text}"
        ),
        "create_explanation":lambda chunk_original_text=None: (
            """
            Please read the following text and generate an explanation suitable for a **basic-level audience**. 
            Use **friendly and everyday language** to make the explanation simple, relatable, and rich with detail. 
            Ensure that the output is written at a **Flesch-Kincaid Grade Level of approximately 6**. 
            Follow the **exact format provided below**, and avoid including any information outside of this structure.
            Include almost all information from the original text, with expanded explanations and examples to enhance understanding.
            
            ### Output Requirements:
            - Write in **clear and relatable language** that a 6th grader could easily understand.
            - Provide **rich and detailed explanations** to make each concept more engaging and informative.
            - Use **examples and analogies** from everyday life to make complex ideas easier to grasp.
            - Ensure the explanation contains **all key points** from the original text, avoiding omissions.
            - Do not add additional comments or unrelated information.
            - Strictly follow the format and headings as shown below.
            
            ---
            
            ## Detailed Explanation:
            ---
            
            ### Stream First Approach
            ---
            This is a way to process data in real-time, like watching a river flow without needing to wait for it to start.  
            Imagine you’re thirsty and want water from a stream. The water is already flowing, so you don’t need to wait—it’s there whenever you need it. In the same way, this approach handles data immediately as it arrives instead of waiting for it to build up.  
            
            - **Why use it?**
              - It’s faster because it doesn’t rely on scheduled tasks that run every 15 minutes or so. Instead, it processes data continuously, like a tap that’s always on.
              - This approach helps people or systems access the data they need without unnecessary delays.
            
            ---
            
            ### Kafka and Event-Driven Applications
            ---
            **Kafka** is like a super-fast courier service that can send and deliver packages (data) almost instantly.  
            **Event-Driven Applications** are like robots that are trained to react whenever something important happens.  
            
            For example:
            - Think of a doorbell. When a friend rings it, a smart robot opens the door right away—that’s how event-driven applications respond to events like new messages or data updates.
            - Kafka works in the background, organizing all the incoming and outgoing messages so that systems can process them efficiently. It ensures no data gets lost or delayed.
            
            ---
            
            ### Change Data Capture (CDC)
            ---
            **CDC** works like a notebook where you jot down every change and immediately share it with others.  
            Imagine you’re updating a class schedule in your notebook. If a teacher changes the time for a lesson, you write it down and immediately show your friends the updated schedule. This is exactly what CDC does for databases—it tracks every change and ensures other systems know about it.
            
            - When data changes, the **CDC Connector** keeps an eye on these updates and shares them with other systems.
            - This ensures that the data is always up-to-date, neat, and ready to use without any confusion or errors.
            
            ---
            
            ### Flink SQL and Data Operations
            ---
            **Flink SQL** is like a powerful calculator that helps you organize, clean, and process data to solve big problems.  
            It’s like when you’re trying to organize a messy room—you need tools like boxes or shelves to sort things out. Flink SQL helps you do that with data.
            
            1. **Denormalization**:
               - Imagine you have a bunch of scattered puzzle pieces. It’s hard to see the full picture until you put them together.
               - Denormalization is like assembling the puzzle so that all the pieces fit into one big picture.
               - For example, if one box has people’s names and another has their addresses, this process combines the two into a single, complete list.
            
            2. **Data Cleaning**:
               - Data cleaning is like tidying up a messy room. You remove unnecessary items, arrange everything neatly, and make it easier to find what you need.
               - With clean data, systems can analyze and process information more effectively, saving time and effort.
            
            ---
            
            Here is the original text to explain:
            """
            f"{chunk_original_text}"
        ),
        "create_feedback": lambda text_list=None: (
            f"""
            Please analyze the explanation generated earlier in this thread and provide detailed feedback based on the following criteria:
            
            ### Feedback Goals:
            
            1. **Simplify the Language**:
               - Evaluate whether the explanation aligns with a Flesch-Kincaid Grade Level of approximately 6.  
               - Highlight specific sentences, phrases, or terms that are overly complex for beginners.  
               - Suggest simpler rephrasing for each identified issue. Use relatable, everyday language and provide examples of revised sentences to make the explanation more accessible.
            
            2. **Ensure IT Terminology Accuracy**:
               - Refer to the `text_list` below for IT-related terms present in the explanation.  
               - Cross-check the explanation of these terms against the original meaning and the target audience's comprehension level.  
               - If any explanation is too complex, rephrase it to match the requested level using relatable metaphors or examples.  
               - Provide feedback on whether the terms are explained clearly and whether the analogies used are appropriate for a beginner-level audience.
            
            3. **Balance Completeness and Simplicity**:
               - Ensure the explanation covers the original content comprehensively without omitting key information.  
               - Comment on whether the explanation is overly simplified to the point of losing essential meaning or if any critical details are missing.  
               - Suggest ways to enrich the explanation while keeping it simple and understandable.
            
            ---
            
            ### Instructions for Feedback Format:
            - Use a structured list format to address each criterion separately.
            - Highlight specific areas of improvement with clear examples and suggestions.
            - Focus only on providing constructive feedback. Do not rewrite the explanation or include unrelated information.
            
            #### Example Feedback Format:
            1. **Simplify the Language**:
               - Sentence: "The stream first approach ensures low-latency real-time processing of data as it flows."  
                 - Feedback: The term "low-latency" may be too technical. Simplify by saying: "The stream first approach processes data quickly as it flows, without waiting."  
            
            2. **Ensure IT Terminology Accuracy**:
               - Term: "Kafka"  
                 - Feedback: The explanation is clear but could use an analogy. Suggestion: Compare Kafka to a postal service that organizes and delivers messages quickly.  
            
            3. **Balance Completeness and Simplicity**:
               - Feedback: The section on "Change Data Capture (CDC)" is clear but lacks an analogy. Suggest adding: "It’s like updating a shared to-do list, so everyone sees the latest tasks immediately."  
            
            ---
            
            ### Text List of Terms to Check:
            {text_list}
            """
        ),
        "create_enhanced_explanation":lambda :(
            """
            Based on the feedback provided, regenerate the explanation to align with the following goals:

            1. **Integrate Feedback**:
               - Address all points highlighted in the feedback. Simplify language where suggested, expand details for clarity or depth, and incorporate missing IT-related terms if indicated.
               - Ensure the updated explanation meets the original intended audience level (e.g., beginner, intermediate, advanced).
            
            2. **Improve Content Quality**:
               - Ensure the explanation is cohesive, accurate, and easy to understand for the intended audience, based on the Flesch-Kincaid Grade Level and the context provided in the feedback.
               - Enhance the content with relatable metaphors, analogies, and examples where appropriate, ensuring the explanation is engaging and understandable.
            
            3. **Follow Format Guidelines**:
               - Highlight the words that appear in the `text_list`.
               - For key conceptual terms, write them in **English, followed by their Korean translation in parentheses**.
               - Provide an explanation of important content related to the topic in **bold italic text**.
               - If the main topic can be divided into subtopics, organize the explanation into separate sections under each subtopic.
               - For step-by-step content, explain each step with **numbered points (e.g., 1., 2., etc.)**, breaking it down into sub-components if necessary.
            
            4. **Provide Korean Translation Only**:
               - Output the explanation in **Korean only**, following the format provided below.
               - Ensure the translation maintains the tone, accessibility, and richness of the original English version.
            
            ### Inputs:
            - **Feedback**: Integrate all the feedback provided earlier in this thread.
            - **Original Explanation**: Use the original explanation as the foundation for your revision.
            - **Text List**: Ensure terms in the text list are explained accurately and are highlighted in both English and Korean.
            
            ### Output Requirements:
            - Provide the explanation directly in **Korean only**, following this format:
            
            ---
            
            # Explanation (Korean):
            ---
            
            ### Stream First Approach (스트림 우선 접근법)
            
            이 방법은 데이터를 **실시간**(real-time)으로 처리하며, 마치 강물이 멈추지 않고 계속 흐르는 것과 같아요. 강물을 기다리지 않아도 항상 흐르고 있는 것처럼, 이 방식은 데이터가 도착하자마자 처리되어 시간을 절약하고 지연을 줄여줍니다.
            
            - **왜 사용할까요?**
              1. 15분마다 데이터를 처리하는 작업을 기다릴 필요가 없어요.
              2. 데이터를 더 빠르게 전달해 사람들이 필요한 정보를 즉시 얻을 수 있도록 도와줘요.
            
            ---
            
            ### Kafka와 Event-Driven Applications (카프카와 이벤트 기반 애플리케이션)
            
            **Kafka**는 메시지(데이터)를 빠르고 신뢰성 있게 전달하는 효율적인 **택배 서비스**(courier service)와 같아요.  
            **Event-Driven Applications**(이벤트 기반 애플리케이션)은 어떤 일이 발생하면 즉시 반응하는 똑똑한 로봇과 같아요.
            
            예를 들어:
            - 친구가 초인종을 누르면 로봇이 바로 문을 열어주는 것처럼, 이벤트 기반 애플리케이션은 데이터가 들어오면 즉시 처리합니다.
            - Kafka는 이러한 메시지를 정리하여 시스템이 효율적으로 처리할 수 있도록 도와줍니다.
            
            ---
            
            ### Change Data Capture (CDC, 데이터 변경 캡처)
            
            **CDC**는 **노트**(notebook)처럼 데이터의 변경 사항을 기록하고, 이를 다른 사람들과 즉시 공유하는 역할을 합니다.  
            예를 들어 선생님이 시간표를 바꿨을 때, 이를 노트에 적고 친구들에게 바로 알려주는 것과 같아요.
            
            - **어떻게 작동할까요?**
              1. **CDC Connector**가 데이터베이스에서 추가, 삭제, 수정과 같은 변경 사항을 감지합니다.
              2. 이러한 변경 사항은 다른 시스템에 전달되어 최신 정보를 항상 유지할 수 있습니다.
            
            ---
            
            ### Flink SQL과 데이터 연산 (Flink SQL and Data Operations)
            
            **Flink SQL**은 데이터를 쉽게 정리하고 처리할 수 있도록 돕는 강력한 **계산기**(calculator)와 같아요. 특히 데이터를 정리하거나 다시 구조화할 때 유용해요.
            
            1. **비정규화(Denormalization)**:
               - 여러 조각으로 흩어진 퍼즐을 생각해 보세요. 전체 그림을 보려면 조각을 하나로 모아야 하죠.
               - 비정규화는 이러한 퍼즐 조각을 모아 한눈에 보기 좋게 만드는 과정이에요.
               - 예를 들어, 한 상자에 이름이 있고 다른 상자에 주소가 있다면, 이를 하나의 목록으로 합치는 거예요.
            
            2. **데이터 정리(Data Cleaning)**:
               - 이것은 마치 어지러운 책상을 정리해서 필요한 것을 쉽게 찾을 수 있도록 만드는 것과 같아요.
               - 데이터를 정리하면 시스템이 더 빠르고 정확하게 작동할 수 있어요.
            
            ---
                """
        )
    }

def _intermediate_prompts():
    return {
        "create_indices": lambda full_original_text=None: (
            """
            Please analyze the following script and identify **very broad, main topics**.

            1. Focus on dividing the script into logical sections based on **major themes or topics**.
            2. Avoid creating overly detailed or granular sections. Combine related content into **larger, meaningful units** to form cohesive topics.
            3. Each topic should represent a **complete logical unit or core concept** and provide sufficient context for understanding.
            4. Aim to identify **5 to 11topics**. Adjust the granularity to ensure the number of topics remains within this range.

            For each topic, provide a start timestamp, title(in english), and short and clear summary(in korean) in a structured JSON format. 
            Make sure topics are ordered by start timestamp
            Follow this exact structure in your output:\n\n
            [{\"startTime\": \"<Start Timestamp>\",\"title\": \"<Topic Title>\",\"summary\": \"<Brief summary of the topic>\"},{\"startTime\": \"<Start Timestamp>\",\"index\": \"<Topic Title>\",\"summary\": \"<Brief summary of the topic>\"},...]
            Output should contain only the JSON array with `start_time`, `index`, and `summary` fields for each topic. 
            Do not add any additional text, explanations, or comments outside of the JSON array.
            double check to make sure summary is in korean and title is english.
            summary should end in form like "~를 설명합니다.", "~를 다룹니다.", "~를 안내합니다."
    
            content start from here:"""
            f"{full_original_text}"
        ),
        "create_explanation": lambda chunk_original_text=None: (
           """
           Here’s the advanced audience prompt rewritten in English, incorporating the structure and elements suited for master’s and doctoral-level students:

            Advanced Audience Prompt
            
            Instructions:
            
            Based on the provided text, generate an explanation tailored for an academic audience at the master’s or doctoral level, specializing in fields such as Computer Science, AI, or Distributed Systems.
            This explanation should emphasize the technical and theoretical depth of the topic, exploring real-world applications, limitations, and recent research trends.
            Basic concepts should be omitted or summarized concisely, using professional terminology, while focusing on in-depth analysis of advanced concepts, trade-offs, and performance evaluations.
            
            Output Requirements:
            
                1.	Target Audience:
                •	Graduate-level students (master’s and doctoral programs) with a background in Computer Science or related fields.
                •	Assume the audience is familiar with basic concepts and frameworks, and focus on providing advanced, in-depth discussions.
                2.	Language and Tone:
                •	Use academically rigorous language with precise and accurate terminology.
                •	Introduce technical terms and concepts appropriately, providing concise context or definitions only if necessary.
                •	Provide a logical and in-depth analysis, avoiding oversimplification while ensuring clarity and coherence.
                3.	Format:
                •	Maintain a structured output with clear headings and subheadings.
                •	Each section must include:
                •	Mechanics and Theoretical Background: Detailed explanations of the concept, supported by algorithms, mathematical models, or system architecture.
                •	Performance Trade-offs and Limitations: Critical analysis of challenges, trade-offs, and how these are addressed in research or practice.
                •	Applications and Research Opportunities: Real-world use cases and potential directions for future research.
                4.	Example Format:
                •	Use the structure below for the explanation:
            
            Detailed Explanation:
            
            Stream-First Approach
            
            Explain the significance of a stream-first approach for real-time data processing in distributed systems, detailing its technical requirements and limitations.
                •	Theoretical Background:
                •	Discuss the low-latency processing model and its foundations in asynchronous computation.
                •	Highlight how stream-first processing differs from traditional batch-oriented architectures, such as the Lambda Architecture.
                •	Performance Analysis and Limitations:
                •	Examine trade-offs between accuracy and system complexity in stream-first vs. batch processing.
                •	Address scalability challenges in streaming systems and solutions like dynamic partitioning.
                •	Applications and Research Opportunities:
                •	Use cases in finance (e.g., real-time trading analysis) and healthcare (e.g., patient monitoring).
                •	Discuss recent advancements in Exactly-Once Delivery guarantees and their implications for real-time systems.
            
            Kafka and Event-Driven Applications
            
            Discuss Kafka as the backbone of event-driven architectures, focusing on its mechanics and role in enabling high-throughput, fault-tolerant communication.
                •	Technical Analysis:
                •	Explain Kafka’s log-based storage model and its implementation of CAP theorem consistency through ISR (In-Sync Replicas).
                •	Analyze the impact of partition rebalancing on performance and fault tolerance.
                •	Applications and Comparative Analysis:
                •	Compare Kafka with alternatives like RabbitMQ, highlighting use cases where each excels.
                •	Discuss real-world examples, such as e-commerce platforms managing real-time inventory updates.
            
            Change Data Capture (CDC)
            
            Explain how CDC enables real-time synchronization between operational and analytical databases, with a focus on its mechanics and limitations.
                •	Mechanics:
                •	Describe how CDC leverages WAL (Write-Ahead Logs) or binary logs to capture database changes.
                •	Address challenges in schema evolution and how CDC tools manage structural consistency.
                •	Limitations and Research Directions:
                •	Explore performance bottlenecks in large-scale CDC implementations and mitigation strategies like index optimizations.
                •	Highlight trade-offs in tools such as Flink or Debezium and their applicability to specific workloads.
            
            Flink SQL and Advanced Operations
            
            Explore the capabilities of Flink SQL for advanced operations on streaming data, focusing on denormalization and data cleaning techniques.
                1.	Denormalization:
                •	Analyze how denormalization improves performance by reducing complex joins.
                •	Discuss trade-offs between normalized and denormalized datasets in terms of storage and processing efficiency.
                2.	Data Cleaning:
                •	Explain the use of windowing functions in Flink SQL for cleaning and aggregating data.
                •	Provide examples, such as cleaning sensor data streams or enriching IoT event streams with metadata.
            
            Additional Requirements:
            
                •	Include examples or scenarios derived from recent research papers or datasets to enhance technical rigor.
                •	Clearly outline the challenges and potential research directions for each topic to inspire further exploration by the audience.
                •	Maintain a logical flow, ensuring the explanation highlights the theoretical and practical significance of the concepts.
            
            This prompt is designed to elicit detailed, academically rich explanations that differentiate themselves through depth of analysis, clarity, and advanced exploration of technical topics, tailored for master’s and doctoral-level audiences.
           """
            f"{chunk_original_text}"
        ),
        "create_feedback": lambda text_list=None: (
            f"""
            Feedback Prompt for Reflection:

Feedback Goals:

	1.	Assess Academic Depth and Use of Terminology:
	•	Determine whether the explanation is suitable for graduate-level students (master’s and doctoral), with an appropriate level of academic depth and precision.
	•	Ensure that:
	•	Basic concepts are either omitted or concisely summarized.
	•	Advanced topics are explained with sufficient technical rigor, incorporating theoretical insights, mathematical models, or research trends.
	•	IT terminology is defined and applied accurately and comprehensively, within a proper academic context.
	2.	Evaluate Professional Tone and Presentation:
	•	Assess whether the explanation conveys a tone of collegial dialogue between professionals, akin to one scholar explaining their research to another.
	•	Feedback should focus on constructive, professional, and encouraging language that respects the academic maturity of the target audience.
	3.	Analyze Technical Integration Examples:
	•	Evaluate whether examples provided highlight how different technical components interact or integrate in real-world applications.
	•	Determine whether the use cases effectively demonstrate the technical and architectural intricacies, beyond serving merely as an aid for easier understanding.
	4.	Prioritize Clear and Actionable Feedback:
	•	Provide feedback that is directly actionable to improve the explanation, focusing on:
	•	Refining the depth and breadth of the content.
	•	Suggesting additional examples, scenarios, or research references where necessary.
	•	Avoid unnecessary critique; instead, provide targeted suggestions for improvement to make the explanation more robust.

Feedback Format:

	1.	Academic Depth and Terminology:
	•	Term or Concept: Specify the technical term or concept.
	•	Feedback: Assess its accuracy, relevance, and depth of explanation, offering suggestions for improvement or expansion.
	2.	Professional Tone:
	•	Section or Sentence: Highlight the specific part of the explanation where tone can be improved.
	•	Feedback: Suggest adjustments to maintain a tone of collegial, academic dialogue.
	3.	Technical Integration Examples:
	•	Example or Use Case: Specify the provided example or scenario.
	•	Feedback: Evaluate whether it effectively demonstrates technical integration or architectural complexity, and recommend enhancements or alternative scenarios.
	4.	Practical Suggestions:
	•	Section or Topic: Mention the relevant part of the explanation.
	•	Feedback: Provide clear, constructive suggestions for improvement, ensuring feedback is actionable and specific.

Example Feedback:

	1.	Academic Depth and Terminology:
	•	Term: “Event-driven architecture”
	•	Feedback: While the term is defined correctly, the explanation could expand on its interaction with microservices. Suggest adding: “Event-driven architectures decouple components through asynchronous communication, which improves scalability. For instance, Kafka topics can act as intermediaries for event propagation, ensuring fault tolerance.”
	2.	Professional Tone:
	•	Sentence: “This is how stream-first processing works; it’s pretty fast and efficient.”
	•	Feedback: The tone feels informal for an academic audience. Suggest revising to: “Stream-first processing enables low-latency data handling by processing events as they arrive, making it a suitable choice for systems requiring real-time responsiveness.”
	3.	Technical Integration Examples:
	•	Example: “Kafka enables real-time event processing in e-commerce systems.”
	•	Feedback: While this is accurate, it lacks depth. Suggest elaborating on how Kafka integrates with components like payment gateways or inventory management systems to ensure consistent state updates across distributed microservices.
	4.	Practical Suggestions:
	•	Section: “Flink SQL for Data Cleaning”
	•	Feedback: The section could benefit from a detailed use case, such as: “Flink SQL can aggregate IoT sensor data, removing outliers and enriching streams with metadata in real-time for predictive maintenance applications.”

Additional Notes:

	•	Ensure all feedback aligns with the graduate-level academic tone and expectations.
	•	Focus on technical rigor and providing actionable insights that enhance the explanation’s depth and practical relevance.
	•	Avoid general or vague comments; make feedback specific and well-structured to guide improvement.
            ---
            
            - Text List of Terms to Check:
            {text_list}
            """
        ),
        "create_enhanced_explanation": lambda: (
            """
            Based on the feedback and explanation generated earlier, regenerate the explanation to align with the following goals:

            1. **Integrate Feedback**:
               - Address all points highlighted in the feedback. Simplify or clarify language where suggested, expand details for conceptual depth, and incorporate missing IT-related terms if indicated.
               - Ensure the updated explanation matches the intended audience level (university students, approximately 1st or 2nd year).
            
            2. **Enhance Content Depth**:
               - Provide a deeper understanding of the concepts, focusing on technical accuracy and real-world applications.
               - Use examples, case studies, or scenarios to demonstrate the relevance and utility of the concepts.
               - Avoid oversimplification, ensuring that critical details are retained and explained effectively.
            
            3. **Follow Format Guidelines**:
               - Highlight words from the `text_list` in the explanation.
               - Write key technical terms in **English, followed by their Korean translation in parentheses**.
               - Emphasize important content using **bold italic text**.
               - Organize the explanation into sections and subtopics with appropriate headings and structured step-by-step descriptions where applicable.
            
            4. **Provide Korean Translation Only**:
               - Output the explanation in **Korean only**, following the format provided below.
               - Ensure the tone, depth, and clarity of the translation align with the original English explanation.
            
            ### Inputs:
            - **Feedback**: Integrate all feedback provided earlier in this thread.
            - **Original Explanation**: Use the original explanation as the foundation for revision.
            - **Text List**: Ensure terms in the text list are explained accurately and are highlighted in both English and Korean.
            
            ### Output Requirements:
            - Provide the explanation in **Korean only**, strictly adhering to the format below:
            
            
            ###example enhanced explanation format begins from here :
            ---
            
            # Explanation (Korean):
            ---
            
            ### Stream First Approach (스트림 우선 접근법)
            
            이 방법은 데이터를 **실시간**(real-time)으로 처리하여 지연 시간을 최소화하고 빠른 결정을 내릴 수 있도록 도와줍니다.  
            예를 들어, 주식 거래 시스템에서는 거래 데이터를 실시간으로 처리하여 투자자가 즉시 반응할 수 있도록 합니다. 이 방식은 데이터가 도착하자마자 처리되며, 대규모 시스템에서의 **낮은 지연 시간**(low latency)을 보장합니다.
            
            - **왜 사용할까요?**
              1. 실시간 데이터 처리로 **사기 탐지**, **IoT 데이터 분석** 등과 같이 즉각적인 반응이 필요한 시스템에서 필수적입니다.
              2. 기존의 배치 처리(batch processing)와 달리, 데이터를 기다리지 않고 즉시 처리하여 실시간 인사이트를 제공합니다.
            
            - **활용 사례**:
              - **금융**: 실시간 주식 거래 분석 및 알림 시스템.
              - **의료**: 환자의 생체 데이터를 실시간으로 분석하여 긴급 상황에 대응.
              - **물류**: 배송 추적 및 실시간 상태 업데이트.
            
            ---
            
            ### Kafka와 Event-Driven Applications (카프카와 이벤트 기반 애플리케이션)
            
            **Kafka**는 고속 메시지 처리와 안정성을 제공하는 **분산 메시지 브로커**(distributed message broker)입니다.  
            **Event-Driven Applications**(이벤트 기반 애플리케이션)은 시스템 간의 비동기식 데이터를 처리하며, 데이터가 발생하는 즉시 반응하도록 설계되었습니다.
            
            - **Kafka의 작동 방식**:
              - Kafka는 **로그 기반 저장소**(log-based storage)를 사용하여 데이터를 순차적으로 저장합니다. 이를 통해 이벤트를 재생(replay)하여 분산 시스템 간의 일관성을 유지할 수 있습니다.
              - 생산자(producer)가 데이터를 **토픽(topic)**에 기록하고, 소비자(consumer)는 이를 구독하여 처리합니다.
            
            - **활용 사례**:
              - **이커머스**: 재고 상태 업데이트와 주문 처리의 실시간 동기화.
              - **게임**: 멀티플레이어 게임에서 플레이어의 상태를 동기화하고 실시간으로 반영.
            
            ---
            
            ### Change Data Capture (CDC, 데이터 변경 캡처)
            
            **Change Data Capture (CDC)**는 데이터베이스의 변경 사항을 실시간으로 추적하고 다른 시스템에 전파하는 기술입니다.  
            이 방법은 운영 시스템과 분석 시스템 간의 **데이터 일관성**(data consistency)을 유지하는 데 중요합니다.
            
            - **작동 원리**:
              1. **CDC 커넥터**는 데이터베이스의 **로그 파일**(log file)을 추적하여 삽입, 수정, 삭제된 데이터를 감지합니다.
              2. 이러한 변경 사항은 **이벤트**로 변환되어 하위 시스템에 전달됩니다.
            
            - **활용 사례**:
              - **데이터 웨어하우스**: 운영 데이터베이스에서 분석 플랫폼(예: Snowflake, Redshift)으로 실시간 데이터 전송.
              - **ETL 파이프라인**: 실시간 데이터 변환 및 통합.
              - **마이크로서비스**: 서로 다른 서비스 간 데이터 동기화.
            
            ---
            
            ### Flink SQL과 데이터 연산 (Flink SQL and Data Operations)
            
            **Flink SQL**은 **스트리밍 데이터**(streaming data)와 **배치 데이터**(batch data)에서 복잡한 연산을 수행할 수 있는 도구입니다.  
            특히, 데이터를 **정리**, **집계**, 그리고 **구조화**하는 데 매우 유용합니다.
            
            1. **비정규화(Denormalization)**:
               - **개념**: 비정규화는 조인(join) 복잡성을 줄이고 데이터를 사전에 집계하여 쿼리 성능을 향상시키는 방법입니다.
               - **활용 사례**: 대시보드에서 빠른 시각화를 위해 여러 테이블의 데이터를 단일 테이블로 통합.
            
            2. **데이터 정리(Data Cleaning)**:
               - **개념**: 데이터 정리는 결측값 처리, 중복 제거 등으로 데이터의 일관성을 확보하는 과정입니다.
               - **활용 사례**: IoT 센서 데이터를 정리하여 잘못된 값이나 이상치를 제거하고 정확한 분석을 수행.
            
            ---
            """
        ),
    }

def _advanced_prompts():
    return {
        "create_indices": lambda full_original_text=None: (
            """
            Please analyze the following script and identify **very broad, main topics**.

            1. Focus on dividing the script into logical sections based on **major themes or topics**.
            2. Avoid creating overly detailed or granular sections. Combine related content into **larger, meaningful units** to form cohesive topics.
            3. Each topic should represent a **complete logical unit or core concept** and provide sufficient context for understanding.
            4. Aim to identify **5 to 9topics** per 30 minute of text. Adjust the granularity to ensure the number of topics remains within this range.

            For each topic, provide a start timestamp, title(in english), and short and clear summary(in korean) in a structured JSON format. 
            Make sure topics are ordered by start timestamp
            Follow this exact structure in your output:\n\n
            [{\"startTime\": \"<Start Timestamp>\",\"title\": \"<Topic Title>\",\"summary\": \"<Brief summary of the topic>\"},{\"startTime\": \"<Start Timestamp>\",\"index\": \"<Topic Title>\",\"summary\": \"<Brief summary of the topic>\"},...]
            Output should contain only the JSON array with `start_time`, `index`, and `summary` fields for each topic. 
            Do not add any additional text, explanations, or comments outside of the JSON array.
            double check to make sure summary is in korean and title is english.
            summary should end in form like "~를 설명합니다.", "~를 다룹니다.", "~를 안내합니다."
    
            content start from here:"""
            f"{full_original_text}"
        ),
        "create_explanation": lambda chunk_original_text=None: (
            """
            Please read the transcription text below and generate an explanation suitable for an **advanced academic audience** (Master's or Ph.D. level in the relevant field).  
            Focus on providing **insight into recent developments**, their **theoretical underpinnings**, and **advanced applications**.  
            Ensure that the explanation supports not only comprehension of the current content but also **guides further research and professional application**.
            
            ### Output Requirements:
            - Use **precise and scholarly language** that reflects the depth of knowledge expected at the graduate level.
            - Emphasize **recent advancements** and **theoretical contexts**, connecting them to the transcription’s subject matter.
            - Incorporate **citations of influential works** (if possible, based on the provided transcription) or explain **key historical developments** leading to the current state of the field.
            - Provide **research directions** and **recommendations for further study** to encourage deeper exploration of the topic.
            - Include a **critical analysis of potential challenges, limitations, or controversies** related to the discussed technologies or theories.
            - Suggest **practical applications** of the concepts in modern industries or research domains, while emphasizing **their transformative potential**.
            
            ### Explanation Structure:
            - Ensure the explanation is structured with well-defined sections and subsections for clarity and logical flow.
            - Maintain a balance between **theoretical analysis**, **practical relevance**, and **future opportunities**.
            - Avoid oversimplification; the focus should be on **comprehensive and nuanced understanding**.
            
            ---
            
            #### Example Format:
            ---
            
            ## Advanced Explanation:
            ---
            
            ### Core Concept Overview
            ---
            Provide a **concise overview** of the core concept discussed in the transcription.  
            - **Definition**: Offer a precise, technical definition of the concept or technology.  
            - **Context**: Place the concept within the broader academic or industrial framework.  
            - **Theoretical Foundations**: Briefly discuss key theories or models that underpin this concept.
            
            ---
            
            ### Recent Developments and Theoretical Insights
            ---
            Analyze **recent advancements** or **emerging trends** related to the concept.  
            - **Key Innovations**: Highlight significant breakthroughs or newly proposed methodologies.  
            - **Foundational Works**: Cite historical or influential studies that contributed to the evolution of the topic.  
            - **Limitations**: Discuss potential shortcomings or areas of active debate within the field.
            
            ---
            
            ### Practical Applications and Case Studies
            ---
            Discuss **real-world applications** of the concept in various domains.  
            - **Examples in Industry**: Provide concrete examples of how the concept is utilized (e.g., in finance, healthcare, AI, or IoT).  
            - **Case Studies**: Reference notable case studies or projects (fictional or generalized if specific citations are not available).  
            - **Transformative Potential**: Explain how this concept could reshape existing systems or paradigms.
            
            ---
            
            ### Research Directions and Further Learning
            ---
            Suggest **directions for further research** and resources for deepening understanding.  
            - **Open Questions**: Identify unresolved questions or challenges in the field.  
            - **Potential Areas for Study**: Recommend related subfields, technologies, or methodologies to explore.  
            - **Suggested Readings**: Provide a list of relevant papers, books, or journals (derived from transcription content or generalized suggestions).
            
            ---
            
            ### Critical Analysis and Future Perspectives
            ---
            Critically evaluate the **broader implications** of the concept.  
            - **Ethical Considerations**: Discuss potential ethical dilemmas or societal impacts.  
            - **Future Trends**: Speculate on how the topic may evolve over the next decade.  
            - **Interdisciplinary Connections**: Highlight how this concept interacts with other fields or technologies.
            
            ---
            
            ### Additional Notes:
            - All sections must align with the transcription content and avoid fabricated citations.
            - If proposing additional learning materials or papers, suggest general directions (e.g., search terms, journals, or key authors) rather than specific titles to prevent inaccuracies.
            ---
            
            - transcription text to explain begins here :
            """
            f"{chunk_original_text}"
        ),
        "create_feedback": lambda text_list=None: (
            f"""
            Please analyze the explanation generated earlier in this thread and provide detailed feedback based on the following criteria:

            ### Feedback Goals:
            
            1. **Depth and Precision of Analysis**:
               - Assess whether the explanation provides a **comprehensive and precise understanding** of the topic suitable for an advanced academic audience (Master's or Ph.D. level).  
               - Identify areas where the explanation could be further deepened or refined to better align with the expectations of advanced learners.  
               - Suggest ways to incorporate **advanced theories, models, or methodologies** to enhance academic rigor.
            
            2. **Relevance and Advancement of Content**:
               - Evaluate whether the explanation effectively discusses **recent developments, emerging trends, or influential theories** related to the topic.  
               - Highlight areas where the explanation could better integrate **current research directions or interdisciplinary connections**.  
               - Ensure the explanation demonstrates how the topic is evolving and its **transformative potential** in research and industry.
            
            3. **Accuracy and Contextualization of Terminology**:
               - Refer to the `text_list` below for IT-related terms present in the explanation.  
               - Cross-check the accuracy and appropriateness of these terms within the advanced academic context.  
               - Provide suggestions for improving definitions or expanding on complex terms with **historical context**, **theoretical significance**, or **practical implications**.
            
            4. **Structure and Logical Flow**:
               - Evaluate whether the explanation is structured to logically progress from foundational concepts to advanced insights and applications.  
               - Identify sections where the flow could be improved or reorganized for greater clarity and coherence.  
               - Suggest adjustments to the headings, subheadings, or organization to better support the narrative.
            
            5. **Practical and Research-Oriented Insights**:
               - Assess whether the explanation provides **practical examples**, **case studies**, or **recommendations for further learning** that align with the needs of advanced learners.  
               - Highlight areas where **real-world applications or research directions** could be more prominently featured or elaborated.  
               - Suggest including examples of **ongoing challenges**, **ethical considerations**, or **future opportunities** to foster critical thinking.
            
            ---
            
            ### Instructions for Feedback Format:
            - Use a structured list format to address each criterion separately.
            - Provide **constructive, actionable feedback** for improvement.
            - Avoid rewriting the explanation or including unrelated information.
            - Focus solely on assessing academic depth, precision, and relevance.
            
            #### Example Feedback Format:
            1. **Depth and Precision of Analysis**:
               - Section: "Core Concept Overview"  
                 - Feedback: While the definition of "stream-first approach" is accurate, it could benefit from a deeper discussion of its mathematical foundations in queueing theory. Suggest adding: "Include a brief explanation of how real-time data pipelines leverage queueing models to optimize throughput and reduce latency."  
            
            2. **Relevance and Advancement of Content**:
               - Section: "Recent Developments and Theoretical Insights"  
                 - Feedback: The explanation lacks references to recent advancements in distributed streaming systems. Suggest incorporating: "Highlight advancements such as the use of AI-driven optimization in stream processing frameworks or the adoption of micro-batching strategies in modern implementations."
            
            3. **Accuracy and Contextualization of Terminology**:
               - Term: "Kafka"  
                 - Feedback: The explanation correctly describes Kafka as a distributed message broker but could expand on its design principles. Suggest adding: "Kafka's partitioned log design ensures scalability and fault tolerance, critical for handling large-scale event-driven systems."
            
            4. **Structure and Logical Flow**:
               - Section: "Practical Applications and Case Studies"  
                 - Feedback: The case studies are relevant but could be better organized under subheadings by industry (e.g., finance, healthcare, logistics) to enhance clarity and accessibility.
            
            5. **Practical and Research-Oriented Insights**:
               - Feedback: While the explanation discusses transformative potential, it lacks recommendations for further research. Suggest including: "Provide examples of key journals, conferences, or research labs pioneering studies in stream processing and event-driven architectures."
            
            ---
            
            ### Text List of Terms to Check:
            {text_list}
            """
        ),
        "create_enhanced_explanation": lambda: (
            """
            Generate a technical and academic-level explanation tailored for senior developers and academic experts.
            Ensure that the explanation reflects the feedback provided and strictly adheres to the following guidelines:
            
            Objectives:
            
                1.	Technical Depth:
                •	Deliver a highly detailed analysis of the core principles, algorithms, and structural mechanisms of the technology.
                •	Focus solely on the technical aspects without introducing practical applications or domain-specific use cases.
                2.	Feedback Integration:
                •	Address gaps or omissions identified in the feedback.
                •	Ensure the explanation demonstrates improved clarity, precision, and completeness.
                3.	Exclude Use Cases:
                •	Refrain from including any specific application examples or scenarios.
                •	Maintain a purely technical focus.
                4.	Encourage Advanced Learning:
                •	Conclude the explanation with a list of advanced keywords or topics to guide further independent exploration.
                5. final language
                • make sure that the final explanation is in korean 
            
            Example Format for the Explanation (Korean):
            
            주제: Kafka의 분산 로그 저장소 아키텍처
            
            Kafka는 **분산 메시지 브로커(distributed message broker)**로, 데이터를 순차적으로 저장하는 **로그 기반 저장소(log-based storage)**를 사용합니다. 이 저장 방식은 데이터 일관성을 유지하면서 높은 처리량을 제공합니다.
            
            1. 아키텍처 및 데이터 흐름:
            
                •	Kafka는 생산자(producer), 소비자(consumer), 파티션(partition) 및 **오프셋(offset)**을 기반으로 작동합니다.
                •	생산자는 데이터를 **토픽(topic)**으로 전송하며, 각 토픽은 다수의 파티션으로 나누어 병렬 처리가 가능합니다.
                •	각 파티션은 **리더-팔로워 구조(leader-follower architecture)**를 사용하여 복제되며, 이는 데이터의 내결함성을 보장합니다.
                •	ISR(In-Sync Replicas) 알고리즘은 리더와 팔로워 간의 복제 동기화를 관리하며, 장애 상황에서 가장 최신 상태의 팔로워가 리더 역할을 수행합니다.
            
            2. 핵심 특징:
            
                1.	재생 가능성(replayability):
                •	Kafka의 로그 기반 저장소는 메시지를 삭제하지 않고 보존하여 과거 데이터를 다시 소비하거나 분석할 수 있습니다.
                •	이는 **상태 복구(state recovery)**와 **이벤트 소싱(event sourcing)**에 유용합니다.
                2.	확장성(scalability):
                •	Kafka는 파티션 수를 동적으로 조정하여 병렬 처리 성능을 선형적으로 확장할 수 있습니다.
            
            3. 트레이드오프:
            
                •	파티션 리밸런싱 비용:
                •	소비자 그룹 내에서 파티션이 재할당될 때 처리 지연이 발생할 수 있습니다.
                •	이를 완화하기 위해 스티키 파티셔닝(sticky partitioning) 기법이 사용됩니다.
                •	저장소 요구:
                •	로그 기반 설계는 장기 메시지 보존 시 높은 저장소 자원을 필요로 하며, 이를 해결하기 위해 **데이터 압축(compaction)**이 활용됩니다.
            
            4. 추가 학습 키워드:
            
                •	ISR(In-Sync Replicas) 알고리즘
                •	Partition Rebalancing 및 Sticky Partitioning
                •	Kafka의 CAP 정리 적용 방식
                •	Kafka의 로그 기반 압축(compaction) 설계
            
            Guidelines:
            
                •	Language: The explanation must be written in Korean.
                •	Tone and Depth: Use an academic tone and provide in-depth analysis suitable for senior developers or academic experts.
                •	Structure: Ensure the explanation is well-structured, with clear sections addressing architecture, features, trade-offs, and learning resources.
                •	Focus: Exclude domain-specific examples or use cases and prioritize technical and theoretical principles.
            
            Evaluation Criteria:
            
                •	Clarity: Is the explanation logically structured and easy to follow?
                •	Depth: Does the explanation thoroughly analyze mechanisms and principles?
                •	Accuracy: Are technical terms correctly defined, and does the explanation align with feedback requirements?
                •	Completeness: Does the explanation address all key aspects without omissions?

            """
        )
    }
