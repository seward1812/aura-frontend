import { useState } from "react";
import axios from "axios";

function UploadRetina({ token, loadHistory }) {

    const [selectedFile, setSelectedFile] =
        useState(null);

    const [result, setResult] =
        useState("");

    const handleUpload = async () => {

        if (!selectedFile) {
            setResult("Please select image");
            return;
        }

        const formData = new FormData();

        formData.append(
            "file",
            selectedFile
        );

        try {

            const response =
                await axios.post(

                    "http://localhost:8080/api/analysis/predict",

                    formData,

                    {
                        headers: {
                            Authorization:
                                `Bearer ${token}`,
                            "Content-Type":
                                "multipart/form-data",
                        },
                    }
                );

            setResult(
                response.data.result
            );

            loadHistory();

        } catch (error) {

            console.log(error);

            setResult(
                "Prediction failed"
            );
        }
    };

    return (

        <div>

            <h2>
                Upload Retina Image
            </h2>

            <input
                type="file"
                onChange={(e) =>
                    setSelectedFile(
                        e.target.files[0]
                    )
                }
            />

            <button
                onClick={handleUpload}
            >
                Predict
            </button>

            {result && (
                <h3>{result}</h3>
            )}

        </div>
    );
}

export default UploadRetina;