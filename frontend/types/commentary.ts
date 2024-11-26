export enum KnowledgeLevel {
    BASIC = "BASIC",
    INTERMEDIATE = "INTERMEDIATE",
    ADVANCED = "ADVANCED"
}

export interface HtmlCommentary {
    startTime: number;
    htmlContent: string;
}

export interface Commentary {
    startTime: number;
    content: string;
}

