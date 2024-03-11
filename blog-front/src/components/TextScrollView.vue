<template>
    <div class='login-view'>
        <div id="marquee">
            <div> <span>杨柳博客</span> </div>
            <div aria-hidden="true"> <span>杨柳博客</span> </div>
        </div>
    </div>
</template>

<style >
#marquee {
    perspective: 800px;
    font-size: 0;
}

#marquee div {
    display: inline-block;
    height: 4rem;
    width: 12rem;
    position: relative;
}

#marquee div:first-of-type {
    background: #e5233e;
    transform-origin: top right;
    transform: rotateY(-40deg);
    color: #fff;
}

#marquee div:last-of-type {
    background: #b31e31;
    transform-origin: top left;
    transform: rotateY(45deg);
    color: #f8c9d9;
}


#marquee div {
    font-size: 3rem;
    overflow: hidden;
}

#marquee div span {
    position: absolute;
    width: 100%;
    line-height: 1.4;
}

#marquee div:first-of-type span {
    transform: translateX(15rem);
    animation: leftcrawl 4s linear infinite;
    text-shadow: 4px 0px 4px rgba(0, 0, 0, 0.3);
}

#marquee div:last-of-type span {
    transform: translateX(0rem);
    animation: rightcrawl 4s linear infinite;
}

@keyframes leftcrawl {
    to {
        transform: translateX(-30rem);
    }
}

@keyframes rightcrawl {
    to {
        transform: translateX(-30rem);
    }
}

@media all and (max-width: 993px) {
    #marquee {
        perspective: none;
    }

    #marquee div:last-of-type {
        opacity: 0;
        height: 0;
    }

    #marquee div:first-of-type {
        width: 80%;
    }
}

</style>
