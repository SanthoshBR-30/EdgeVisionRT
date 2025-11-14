// EdgeVisionRT Web Viewer
// Displays processed frames from Android app

interface FrameStats {
    resolution: string;
    fps: number;
    processing: string;
}

class FrameViewer {
    private frameElement: HTMLImageElement;
    private resolutionElement: HTMLElement;
    private fpsElement: HTMLElement;
    private statusElement: HTMLElement;

    constructor() {
        this.frameElement = document.getElementById('frame-display') as HTMLImageElement;
        this.resolutionElement = document.getElementById('resolution') as HTMLElement;
        this.fpsElement = document.getElementById('fps') as HTMLElement;
        this.statusElement = document.getElementById('status') as HTMLElement;

        this.initialize();
    }

    private initialize(): void {
        console.log('EdgeVisionRT Web Viewer initialized');
        this.updateStatus('Waiting for frame data...');

        // Load sample processed frame (placeholder for now)
        this.loadSampleFrame();
    }

    private loadSampleFrame(): void {
        // Placeholder: Gray rectangle representing processed frame
        // In real implementation, this will receive base64 image from Android
        const canvas = document.createElement('canvas');
        canvas.width = 640;
        canvas.height = 480;
        const ctx = canvas.getContext('2d');

        if (ctx) {
            // Draw placeholder
            ctx.fillStyle = '#333';
            ctx.fillRect(0, 0, 640, 480);
            ctx.fillStyle = '#fff';
            ctx.font = '24px Arial';
            ctx.textAlign = 'center';
            ctx.fillText('Processed Frame Placeholder', 320, 240);

            this.frameElement.src = canvas.toDataURL();
            this.updateStats({ resolution: '640x480', fps: 15, processing: 'Edge Detection' });
            this.updateStatus('Sample frame loaded');
        }
    }

    public updateFrame(base64Image: string): void {
        this.frameElement.src = base64Image;
        this.updateStatus('Frame updated');
    }

    public updateStats(stats: FrameStats): void {
        this.resolutionElement.textContent = stats.resolution;
        this.fpsElement.textContent = stats.fps.toString();
    }

    private updateStatus(status: string): void {
        this.statusElement.textContent = status;
        console.log(`Status: ${status}`);
    }
}

// Initialize viewer when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    const viewer = new FrameViewer();
    console.log('Web viewer ready');
});