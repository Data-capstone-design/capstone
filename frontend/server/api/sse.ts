import { noteIndex } from "~/server/models/noteIndex";
import { commentaries } from "~/server/models/commentary";
export interface NoteIndexItem {
    startTime: number,
    title: string,
    summary: string
}

export default defineEventHandler((event) => {
    const { req, res } = event.node;

    res.writeHead(200, {
        "Content-Type": "text/event-stream",
        "Cache-Control": "no-cache",
        Connection: "keep-alive",
    });

    let i = 0;

    const sendData = () => {
        if (i >= commentaries.length) {
            res.write(`event: close\n`);
            res.write(`data: "End of commentary stream"\n\n`);

            clearInterval(intervalId);
            res.end();
            return;
        }

        const commentary = commentaries[i];

        res.write(`event: commentary\n`);
        res.write(`data: ${JSON.stringify(commentary)}\n\n`);

        res.write(`event: index\n`);
        res.write(`data: ${JSON.stringify({ noteIndex })}\n\n`);
        i += 1;
    };

    const intervalId = setInterval(sendData,300);

    req.on("close", () => {
        clearInterval(intervalId);
        res.end();
    });
});

