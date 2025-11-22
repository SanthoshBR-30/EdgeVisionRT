

class FrameViewer {
    private rawFrameElement: HTMLImageElement;
    private edgeFrameElement: HTMLImageElement;

    constructor() {
        this.rawFrameElement = document.getElementById('raw-display') as HTMLImageElement;
        this.edgeFrameElement = document.getElementById('edge-display') as HTMLImageElement;

        this.initialize();
    }

    private initialize(): void {
        console.log('EdgeVisionRT Web Viewer initialized');
        console.log('Timestamp:', new Date().toISOString());

        // Log when images load
        this.rawFrameElement.onload = () => {
            console.log('Raw frame loaded successfully');
        };

        this.edgeFrameElement.onload = () => {
            console.log('Edge frame loaded successfully');
        };

        this.rawFrameElement.onerror = () => {
            console.warn('Raw frame not found: raw_frame.png');
        };

        this.edgeFrameElement.onerror = () => {
            console.warn('Edge frame not found: edge_frame.png');
        };

        console.log('%c📸 EdgeVisionRT Web Viewer Ready', 'font-size: 16px; font-weight: bold; color: #667eea');
        console.log('Waiting for images:');
        console.log('  - raw_frame.png (Raw camera feed)');
        console.log('  - edge_frame.png (Edge detection output)');
    }
}

// Initialize viewer when DOM is ready
document.addEventListener('DOMContentLoaded', () => {
    const viewer = new FrameViewer();

    // Expose viewer globally
    (window as any).frameViewer = viewer;

    console.log('Web viewer initialized and ready');
});