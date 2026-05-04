/**
 * 
 */
function openmanualPanel(){
		var d =document.getElementById("manualPanel");
	   d.hidden=false;
	}
	function closemanualPanel(){
		var d =document.getElementById("manualPanel");
	   d.hidden=true;
	}
	var current = 0;
	 
	
		 
	function initDraggable(elementId,imgid){
		 const draggableDiv = document.getElementById(elementId);
        const draggableImg = document.getElementById(imgid);
        
        let isDragging = false;  // 是否正在拖拽
        let startX = 0, startY = 0;  // 鼠标/触摸的初始位置
        let offsetX = 0, offsetY = 0;  // 元素的偏移量
        const DRAG_THRESHOLD = 5;  // 拖拽与点击的距离阈值

        // 恢复位置
        function restorePosition() {
            const savedPosition = localStorage.getItem('draggableDivPosition');
            if (savedPosition) {
                const { left, top } = JSON.parse(savedPosition);
                console.log(left+"----"+top)
                if(left<=0||top<=0||left>1000||top>1000){
					draggableDiv.style.left = `50px`;
                draggableDiv.style.top = `50px`;
				}else{
					  draggableDiv.style.left = `${left}px`;
                draggableDiv.style.top = `${top}px`;
				}
              
            } else {
                draggableDiv.style.left = '50px';
                draggableDiv.style.top = '50px';
            }
        }

        // 保存位置
        function savePosition(left, top) {
            const position = { left, top };
            localStorage.setItem('draggableDivPosition', JSON.stringify(position));
        }

        // 点击事件
        draggableImg.addEventListener('click', (e) => {
            if (!isDragging) {
				current = (current+45)%360;
	//let img=document.getElementById(appselectLabel);
                draggableImg.style.transform = 'rotate('+current+'deg)'; 
                var d =document.getElementById("selectapp");
                if(d.hidden===false){
					closeselect(d)
				}else{
					openselect(d)
				} 
            }
             function openselect(d){
		
	   d.hidden=false;
	}
	function closeselect(d){
		
	   d.hidden=true;
	}
        });

        // 开始拖动（鼠标）
        draggableImg.addEventListener('mousedown', (e) => {
            isDragging = false;
            startX = e.clientX;
            startY = e.clientY;
            offsetX = e.clientX - draggableDiv.offsetLeft;
            offsetY = e.clientY - draggableDiv.offsetTop;

            draggableDiv.style.cursor = "grabbing";
            e.preventDefault();
        });

        // 拖动中（鼠标）
        window.addEventListener('mousemove', (e) => {
            if (startX === 0 && startY === 0) return;  // 确保拖拽已经开始
            const moveX = e.clientX - startX;
            const moveY = e.clientY - startY;
            const distance = Math.sqrt(moveX ** 2 + moveY ** 2);

            if (distance > DRAG_THRESHOLD) {
                isDragging = true;
                const newLeft = e.clientX - offsetX;
                const newTop = e.clientY - offsetY;

                draggableDiv.style.left = `${newLeft}px`;
                draggableDiv.style.top = `${newTop}px`;

                savePosition(newLeft, newTop);
            }
        });

        // 结束拖动（鼠标）
        window.addEventListener('mouseup', () => {
            if (isDragging) {
                isDragging = false;
            }
            startX = 0;
            startY = 0;  // 清除初始位置
            draggableDiv.style.cursor = "grab";
        });
        

        // 开始拖动（触摸）
        draggableImg.addEventListener('touchstart', (e) => {
            isDragging = false;
            const touch = e.touches[0];
            startX = touch.clientX;
            startY = touch.clientY;
            offsetX = touch.clientX - draggableDiv.offsetLeft;
            offsetY = touch.clientY - draggableDiv.offsetTop;

            e.preventDefault();
            current = (current+45)%360;
	//let img=document.getElementById(appselectLabel);
                draggableImg.style.transform = 'rotate('+current+'deg)'; 
                var d =document.getElementById("selectapp");
                if(d.hidden===false){
					closeselect(d)
				}else{
					openselect(d)
				} 
            
             function openselect(d){
		
	   d.hidden=false;
	}
	function closeselect(d){
		
	   d.hidden=true;
	}
            
        });

        // 拖动中（触摸）
        window.addEventListener('touchmove', (e) => {
            if (startX === 0 && startY === 0) return;  // 确保拖拽已经开始
            const touch = e.touches[0];
            const moveX = touch.clientX - startX;
            const moveY = touch.clientY - startY;
            const distance = Math.sqrt(moveX ** 2 + moveY ** 2);

            if (distance > DRAG_THRESHOLD) {
                isDragging = true;
                const newLeft = touch.clientX - offsetX;
                const newTop = touch.clientY - offsetY;

                draggableDiv.style.left = `${newLeft}px`;
                draggableDiv.style.top = `${newTop}px`;

                savePosition(newLeft, newTop);
            }
					
			
        });

        // 结束拖动（触摸）
        window.addEventListener('touchend', () => {
            if (isDragging) {
                isDragging = false;
            }
            startX = 0;
            startY = 0;  // 清除初始位置
        });

        // 初始化
        restorePosition();

	}
	
		
	 