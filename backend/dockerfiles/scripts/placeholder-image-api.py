
import base64
from io import BytesIO
from flask import Flask, request, jsonify
from PIL import Image, ImageDraw, ImageFont

app = Flask(__name__)

# --- Configuration ---
IMG_WIDTH = 512
IMG_HEIGHT = 512
BACKGROUND_COLOR = (100, 100, 100)
TEXT_COLOR = (255, 255, 255)
FONT_SIZE = 20
try:
    # Use a common, often pre-installed font.
    font = ImageFont.truetype("DejaVuSans.ttf", FONT_SIZE)
except IOError:
    # Fallback to default font if not found.
    font = ImageFont.load_default()

@app.route('/health', methods=['GET'])
def health_check():
    """
    A simple health check endpoint.
    """
    return jsonify({"status": "healthy"}), 200

@app.route('/sdapi/v1/txt2img', methods=['POST'])
def generate_placeholder_image():
    """
    Mimics the AUTOMATIC1111 API.
    Generates a placeholder image with the prompt text written on it.
    """
    try:
        payload = request.get_json()
        prompt_text = payload.get('prompt', 'No prompt provided')

        # Create a new image
        image = Image.new('RGB', (IMG_WIDTH, IMG_HEIGHT), color=BACKGROUND_COLOR)
        draw = ImageDraw.Draw(image)

        # Wrap text and draw it on the image
        # This is a simple text wrapper; a more complex one could be used if needed.
        lines = []
        words = prompt_text.split()
        current_line = ''
        for word in words:
            if draw.textlength(current_line + word, font=font) <= IMG_WIDTH - 20:
                current_line += word + ' '
            else:
                lines.append(current_line)
                current_line = word + ' '
        lines.append(current_line)

        y_text = 10
        for line in lines:
            draw.text((10, y_text), line.strip(), font=font, fill=TEXT_COLOR)
            y_text += FONT_SIZE + 5

        # Convert image to a Base64 string
        buffered = BytesIO()
        image.save(buffered, format="PNG")
        img_str = base64.b64encode(buffered.getvalue()).decode('utf-8')

        # Mimic the exact response structure of the AUTOMATIC1111 API
        response_data = {
            "images": [img_str],
            "parameters": {},
            "info": ""
        }

        return jsonify(response_data)

    except Exception as e:
        # Log the error and return a standard error response
        print(f"Error generating placeholder image: {e}")
        return jsonify({"error": "Failed to process request"}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=7860)
