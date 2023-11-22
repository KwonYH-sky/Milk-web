window.onload = () => {
  const options = {
    modules: {
      toolbar: {
        container: [
          [{ header: [1, 2, false] }],
          ["bold", "italic", "underline"],
          ["image", "video", "code-block"],
        ],
      },
    },
    placeholder: "여기부터 작성해주세요...",
    theme: "snow",
  };
  const editor = new Quill("#editor", options);

  // input 태그 값과 동기화
  editor.on("text-change", () => {
    document.getElementById("content").value = editor.root.innerHTML;
  });
  
  editor.getModule("toolbar").addHandler("image", function () {
    imageHandler(editor);
  });
};

const imageHandler = (quill) => {
  const header = $("meta[name='_csrf_header']").attr("content");
  const token = $("meta[name='_csrf']").attr("content");

  const input = document.createElement("input");
  input.setAttribute("type", "file");
  input.setAttribute("accept", "image/*");
  input.click();

  input.addEventListener("change", async () => {
    const formData = new FormData();
    const file = input.files[0];

    formData.append("img", file);

    try {
      const result = await axios.post("/board/uploadImg", formData, {
        headers: {
          "Content-Type": "multipart/form-data; charset=utf-8",
          [header]: token,
        },
      });
      console.log(result);
      console.log(quill);
      const imgName = result.data.imgName;

      const range = quill.getSelection();

      quill.insertEmbed(
        range.index,
        "image",
        "/board/images?imgName=" + imgName
      );
    } catch (err) {
      console.log(err);
    }
  });
};
